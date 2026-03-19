package com.innowise.userservice.service.impl;

import com.innowise.userservice.exception.cardexception.*;
import com.innowise.userservice.exception.userexception.NotActiveUserException;
import com.innowise.userservice.exception.userexception.UserNotFoundException;
import com.innowise.userservice.repository.PaymentCardDao;
import com.innowise.userservice.repository.UserDao;
import com.innowise.userservice.dto.PaymentCardDto;
import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.service.PaymentCardService;
import com.innowise.userservice.specification.PaymentCardSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.data.redis.autoconfigure.DataRedisProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentCardServiceImpl implements PaymentCardService {

    public static final int MAX_COUNT_ACTIVE_CARDS = 5;

    private final PaymentCardDao paymentCardDao;
    private final UserDao userDao;
    private final PaymentCardMapper paymentCardMapper;
    private final CacheManager cacheManager;

    @Override
    @Transactional
    @CacheEvict(cacheNames = "users", key = "#paymentCardDto.userId")
    public PaymentCardDto createPaymentCard(PaymentCardDto paymentCardDto) {

        if(paymentCardDto == null){
            throw new CardNullParameterException();
        }

        String cardNumber = paymentCardDto.getNumber();

        boolean isExist = paymentCardDao.existsByNumber(cardNumber);

        if(isExist){
            PaymentCard cardByNumber = paymentCardDao.findPaymentCardByNumber(cardNumber)
                    .orElseThrow(() -> new CardNotFoundException(cardNumber));

            if(cardByNumber.isActive()){
                throw new ExistCardException(cardNumber);
            }
        }

        Long userId = paymentCardDto.getUserId();

        User user = userDao.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if(!user.isActive()){
            throw new NotActiveUserException(userId);
        }

        int countOfActiveCards = paymentCardDao.countActivePaymentCardByUserId(userId);

        if(countOfActiveCards >= MAX_COUNT_ACTIVE_CARDS){
            throw new MaxCountCardsException(MAX_COUNT_ACTIVE_CARDS);
        }

        PaymentCard paymentCard = paymentCardMapper.toPaymentCard(paymentCardDto);
        paymentCard.setUser(user);

        PaymentCard savedCard = paymentCardDao.save(paymentCard);

        return paymentCardMapper.toPaymentCardDto(savedCard);
    }


    @Override
    @Transactional(readOnly = true)
    public PaymentCardDto getPaymentCardById(Long id) {

        if(id == null){
            throw new CardNullParameterException();
        }

        PaymentCard paymentCard = paymentCardDao.findPaymentCardById(id)
                .orElseThrow(() -> new CardNotFoundException(id));

        return paymentCardMapper.toPaymentCardDto(paymentCard);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentCardDto> getAllPaymentCardsByUserId(Long id) {

        if(id == null){
            throw new CardNullParameterException();
        }

        userDao.findUserById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        List<PaymentCard> paymentCards = paymentCardDao.findAllByUserId(id);

        return paymentCards.stream()
                .map(paymentCardMapper::toPaymentCardDto)
                .toList();

    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentCardDto> getAllPaymentCards(String number, Pageable pageable) {

        if(pageable == null){
            throw new CardNullParameterException();
        }

        Specification<PaymentCard> paymentCardSpecification = Specification
                .where(PaymentCardSpecification.byNumber(number))
                .and(PaymentCardSpecification.isActive());

        Page<PaymentCard> paymentCardPage = paymentCardDao.findAll(paymentCardSpecification, pageable);

        return paymentCardPage.map(paymentCardMapper::toPaymentCardDto);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "users", key = "#paymentCardDto.userId")
    public PaymentCardDto updatePaymentCard(PaymentCardDto paymentCardDto) {
        if(paymentCardDto == null){
            throw new CardNullParameterException();
        }

        Long cardId = paymentCardDto.getId();
        Long userId = paymentCardDto.getUserId();

        User user = userDao.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if(!user.isActive()){
            throw new NotActiveUserException(userId);
        }

        PaymentCard paymentCard = paymentCardDao.findPaymentCardById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));

        if(!paymentCard.isActive()){
            throw new NotActiveCardException(cardId);
        }

        paymentCardMapper.updatePaymentCardFromDto(paymentCardDto, paymentCard);
        PaymentCard updatedCard = paymentCardDao.save(paymentCard);

        return paymentCardMapper.toPaymentCardDto(updatedCard);
    }

    @Override
    @Transactional
    public boolean activatePaymentCardById(Long id) {
        if (id == null){
            throw new CardNullParameterException();
        }

        PaymentCard paymentCard = paymentCardDao.findPaymentCardById(id)
                .orElseThrow(() -> new CardNotFoundException(id));

        User user = paymentCard.getUser();
        Long userId = user.getId();

        if(!user.isActive()){
            throw new NotActiveUserException(userId);
        }

        int countActiveCards = paymentCardDao.countActivePaymentCardByUserId(userId);

        if(countActiveCards >= MAX_COUNT_ACTIVE_CARDS){
            throw new MaxCountCardsException(MAX_COUNT_ACTIVE_CARDS);
        }

        int success = paymentCardDao.activatePaymentCardById(id);

        if(success == 0){
            throw new CardActivateException(id);
        }

        Cache usersCache = cacheManager.getCache("users");
        if(usersCache != null){
            usersCache.evict(userId);
        }

        return true;
    }

    @Override
    @Transactional
    public boolean deactivatePaymentCardById(Long id) {
        if (id == null){
            throw new CardNullParameterException();
        }

        PaymentCard paymentCard = paymentCardDao.findPaymentCardById(id)
                .orElseThrow(() -> new CardNotFoundException(id));

        Long userId = paymentCard.getUser().getId();

        int success = paymentCardDao.deactivatePaymentCardById(id);

        if(success == 0){
            throw new CardDeactivateException(id);
        }

        Cache usersCache = cacheManager.getCache("users");
        if(usersCache != null){
            usersCache.evict(userId);
        }

        return true;
    }
}
