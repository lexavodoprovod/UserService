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
import jakarta.validation.constraints.NotNull;
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

    private final int MAX_COUNT_CARDS = 5;

    private final PaymentCardDao paymentCardDao;
    private final UserDao userDao;
    private final PaymentCardMapper paymentCardMapper;

    @Override
    @Transactional
    public PaymentCardDto createPaymentCard(PaymentCardDto paymentCardDto) {

        if(paymentCardDto == null){
            throw new BusinessException("[createPaymentCard] PaymentCardDto is null");
        }

        boolean isExist = paymentCardDao.existsByNumber(paymentCardDto.getNumber());

        if(isExist){
           throw new BusinessException("PaymentCard with this number already exists");
        }

        Long userId = paymentCardDto.getUserId();

        User user = userDao.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));

        int count = paymentCardDao.countPaymentCardByUserId(userId);

        if(count > MAX_COUNT_CARDS){
            throw new BusinessException("User cannot have more than " + MAX_COUNT_CARDS + " paymentCards");
        }

        PaymentCard paymentCard = paymentCardMapper.toPaymentCard(paymentCardDto);
        paymentCard.setUser(user);

        PaymentCard paymentCardSaved = paymentCardDao.save(paymentCard);

        return paymentCardMapper.toPaymentCardDto(paymentCardSaved);
    }


    @Override
    @Transactional
    public PaymentCardDto getPaymentCardById(Long id) {

        if(id == null){
            throw new BusinessException("[gitPaymentCardById] Id is null");
        }

        PaymentCard paymentCard = paymentCardDao.findPaymentCardById(id)
                .orElseThrow(() -> new EntityNotFoundException("PaymentCard", id));

        return paymentCardMapper.toPaymentCardDto(paymentCard);
    }

    @Override
    @Transactional
    public List<PaymentCardDto> getAllPaymentCardsByUserId(Long id) {

        if(id == null){
            throw new BusinessException("[getPaymentCardsByUserId] Id is null");
        }

        List<PaymentCard> paymentCards = paymentCardDao.findAllByUserId(id);

        return paymentCards.stream()
                .map(paymentCardMapper::toPaymentCardDto)
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public Page<PaymentCardDto> getAllPaymentCards(String number, Pageable pageable) {

        if(number == null || pageable == null){
            throw new BusinessException("[getAllPaymentCardsByUserId] Pageable or number is null");
        }

        Specification<PaymentCard> paymentCardSpecification = PaymentCardSpecification.byNumber(number);

        Page<PaymentCard> paymentCardPage = paymentCardDao.findAll(paymentCardSpecification, pageable);

        return paymentCardPage.map(paymentCardMapper::toPaymentCardDto);
    }

    @Override
    @Transactional
    public PaymentCardDto updatePaymentCard(PaymentCardDto paymentCardDto) {
        if(paymentCardDto == null){
            throw new BusinessException("[updatePaymentCard] PaymentCardDto is null");
        }

        Long id = paymentCardDto.getId();

        PaymentCard paymentCard = paymentCardDao.findPaymentCardById(id)
                .orElseThrow(() -> new EntityNotFoundException("PaymentCard", id));

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

        int success = paymentCardDao.activatePaymentCardById(id);

        return success !=0;
    }

    @Override
    @Transactional
    public boolean deactivatePaymentCardById(Long id) {
        if (id == null){
            throw new BusinessException("[deactivatePaymentCardById] Id is null");
        }

        int success = paymentCardDao.deactivatePaymentCardById(id);

        return success !=0;
    }
}
