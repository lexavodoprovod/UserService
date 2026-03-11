package com.innowise.UserService.model.service.impl;

import com.innowise.UserService.model.dao.PaymentCardDao;
import com.innowise.UserService.model.dto.PaymentCardDto;
import com.innowise.UserService.model.entity.PaymentCard;
import com.innowise.UserService.model.mapper.PaymentCardMapper;
import com.innowise.UserService.model.service.PaymentCardService;
import com.innowise.UserService.model.specification.PaymentCardSpecification;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NotNull
@RequiredArgsConstructor
public class PaymentCardServiceImpl implements PaymentCardService {

    private final PaymentCardDao paymentCardDao;
    private final PaymentCardMapper paymentCardMapper;

    @Override
    @Transactional
    public PaymentCardDto createPaymentCard(PaymentCardDto paymentCardDto) {
        if(paymentCardDto == null){
            return null;
        }

        String number = paymentCardDto.getNumber();

        if(!paymentCardDao.existsByNumber(number)){
            PaymentCard paymentCard = paymentCardMapper.toPaymentCard(paymentCardDto);

            paymentCardDao.save(paymentCard);

            return paymentCardMapper.toPaymentCardDto(paymentCard);
        }

        return null;
    }


    @Override
    @Transactional
    public PaymentCardDto getPaymentCardById(Long id) {

        if(id == null){
            return null;
        }

        Optional<PaymentCard> paymentCardOpt = paymentCardDao.findPaymentCardById(id);

        if(paymentCardOpt.isPresent()){
            PaymentCard paymentCard = paymentCardOpt.get();
            paymentCardDao.save(paymentCard);

            return paymentCardMapper.toPaymentCardDto(paymentCard);
        }

        return null;
    }

    @Override
    @Transactional
    public List<PaymentCardDto> getPaymentCardsByUserId(Long id) {

        if(id == null){
            return null;
        }

        List<PaymentCard> paymentCards = paymentCardDao.findAllByUserId(id);

        return paymentCards.stream()
                .map(paymentCardMapper::toPaymentCardDto)
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public Page<PaymentCardDto> getPaymentCardsByUserId(String number, Pageable pageable) {
        Specification<PaymentCard> paymentCardSpecification = PaymentCardSpecification.byNumber(number);

        Page<PaymentCard> paymentCardPage = paymentCardDao.findAll(paymentCardSpecification, pageable);

        return paymentCardPage.map(paymentCardMapper::toPaymentCardDto);
    }

    @Override
    @Transactional
    public PaymentCardDto updatePaymentCard(PaymentCardDto paymentCardDto) {
        if(paymentCardDto == null){
            return null;
        }

        Long id = paymentCardDto.getId();

        Optional<PaymentCard> paymentCardOpt = paymentCardDao.findPaymentCardById(id);

        if(paymentCardOpt.isPresent()){
            PaymentCard paymentCard = paymentCardOpt.get();

            paymentCardMapper.updatePaymentCardFromDto(paymentCardDto, paymentCard);

            paymentCardDao.updatePaymentCardById(paymentCard);

            return paymentCardDto;
        }
        return null;
    }

    @Override
    @Transactional
    public boolean activatePaymentCardById(Long id) {
        if (id == null){
            return false;
        }

        int success = paymentCardDao.activatePaymentCardById(id);

        return success !=0;
    }

    @Override
    @Transactional
    public boolean deactivatePaymentCardById(Long id) {
        if (id == null){
            return false;
        }

        int success = paymentCardDao.deactivatePaymentCardById(id);

        return success !=0;
    }
}
