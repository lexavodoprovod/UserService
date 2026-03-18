package com.innowise.UserService.model.service.impl;

import com.innowise.UserService.exception.BusinessException;
import com.innowise.UserService.exception.EntityNotFoundException;
import com.innowise.UserService.model.dao.PaymentCardDao;
import com.innowise.UserService.model.dao.UserDao;
import com.innowise.UserService.model.dto.PaymentCardDto;
import com.innowise.UserService.model.entity.PaymentCard;
import com.innowise.UserService.mapper.PaymentCardMapper;
import com.innowise.UserService.model.entity.User;
import com.innowise.UserService.model.service.PaymentCardService;
import com.innowise.UserService.specification.PaymentCardSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentCardServiceImpl implements PaymentCardService {

    private final int MAX_COUNT_ACTIVE_CARDS = 5;

    private final PaymentCardDao paymentCardDao;
    private final UserDao userDao;
    private final PaymentCardMapper paymentCardMapper;

    @Override
    @Transactional
    public PaymentCardDto createPaymentCard(PaymentCardDto paymentCardDto) {

        if(paymentCardDto == null){
            throw new BusinessException("[createPaymentCard] PaymentCardDto is null");
        }

        String cardNumber = paymentCardDto.getNumber();

        boolean isExist = paymentCardDao.existsByNumber(cardNumber);

        if(isExist){
            PaymentCard cardByNumber = paymentCardDao.findPaymentCardByNumber(cardNumber)
                    .orElseThrow(() -> new EntityNotFoundException("Error find card with this number"));

            if(cardByNumber.isActive()){
                throw new BusinessException("PaymentCard with this number already exists");
            }
        }

        Long userId = paymentCardDto.getUserId();

        User user = userDao.findUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));

        if(!user.isActive()){
            throw new BusinessException("User with id[%s] is not active]".formatted(userId));
        }

        int countOfActiveCards = paymentCardDao.countActivePaymentCardByUserId(userId);

        if(countOfActiveCards >= MAX_COUNT_ACTIVE_CARDS){
            throw new BusinessException("User cannot have more than " + MAX_COUNT_ACTIVE_CARDS + " active paymentCards");
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
            throw new BusinessException("[getPaymentCardById] Id is null");
        }

        PaymentCard paymentCard = paymentCardDao.findPaymentCardById(id)
                .orElseThrow(() -> new EntityNotFoundException("PaymentCard", id));

        return paymentCardMapper.toPaymentCardDto(paymentCard);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentCardDto> getAllPaymentCardsByUserId(Long id) {

        if(id == null){
            throw new BusinessException("[getPaymentCardsByUserId] Id is null");
        }

        userDao.findUserById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id));

        List<PaymentCard> paymentCards = paymentCardDao.findAllByUserId(id);

        return paymentCards.stream()
                .map(paymentCardMapper::toPaymentCardDto)
                .collect(Collectors.toList());

    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentCardDto> getAllPaymentCards(String number, Pageable pageable) {

        if(pageable == null){
            throw new BusinessException("[getAllPaymentCards] Pageable or number is null");
        }

        Specification<PaymentCard> paymentCardSpecification = Specification
                .where(PaymentCardSpecification.byNumber(number))
                .and(PaymentCardSpecification.isActive());

        Page<PaymentCard> paymentCardPage = paymentCardDao.findAll(paymentCardSpecification, pageable);

        return paymentCardPage.map(paymentCardMapper::toPaymentCardDto);
    }

    @Override
    @Transactional
    public PaymentCardDto updatePaymentCard(PaymentCardDto paymentCardDto) {
        if(paymentCardDto == null){
            throw new BusinessException("[updatePaymentCard] PaymentCardDto is null");
        }

        Long cardId = paymentCardDto.getId();
        Long userId = paymentCardDto.getUserId();

        User user = userDao.findUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));

        if(!user.isActive()){
            throw new BusinessException("User with id[%s] is not active".formatted(userId));
        }

        PaymentCard paymentCard = paymentCardDao.findPaymentCardById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("PaymentCard", cardId));

        if(!paymentCard.isActive()){
            throw new BusinessException("PaymentCard with id[%s] is not active".formatted(userId));
        }

        paymentCardMapper.updatePaymentCardFromDto(paymentCardDto, paymentCard);
        paymentCardDao.updatePaymentCardById(paymentCard);

        return paymentCardMapper.toPaymentCardDto(paymentCard);
    }

    @Override
    @Transactional
    public boolean activatePaymentCardById(Long id) {
        if (id == null){
            throw new BusinessException("[activatePaymentCardById] Id is null");
        }

        PaymentCard paymentCard = paymentCardDao.findPaymentCardById(id)
                .orElseThrow(() -> new EntityNotFoundException("PaymentCard", id));

        User user = paymentCard.getUser();

        if(!user.isActive()){
            throw new BusinessException("User with id[%s] is not active".formatted(user.getId()));
        }

        int countActiveCards = paymentCardDao.countActivePaymentCardByUserId(user.getId());

        if(countActiveCards >= MAX_COUNT_ACTIVE_CARDS){
            throw new BusinessException("User cannot have more than " + MAX_COUNT_ACTIVE_CARDS + " active paymentCards");
        }

        int success = paymentCardDao.activatePaymentCardById(id);

        return success !=0;
    }

    @Override
    @Transactional
    public boolean deactivatePaymentCardById(Long id) {
        if (id == null){
            throw new BusinessException("[deactivatePaymentCardById] Id is null");
        }

        PaymentCard paymentCard = paymentCardDao.findPaymentCardById(id)
                .orElseThrow(() -> new EntityNotFoundException("PaymentCard", id));

        int success = paymentCardDao.deactivatePaymentCardById(id);

        return success !=0;
    }
}
