package com.innowise.userservice.model.service;

import com.innowise.userservice.model.dto.PaymentCardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentCardService {
    PaymentCardDto createPaymentCard(PaymentCardDto paymentCardDto);

    PaymentCardDto getPaymentCardById(Long id);

    List<PaymentCardDto> getAllPaymentCardsByUserId(Long id);

    Page<PaymentCardDto> getAllPaymentCards(String number, Pageable pageable);

    PaymentCardDto updatePaymentCard(PaymentCardDto paymentCardDto);

    boolean activatePaymentCardById(Long id);

    boolean deactivatePaymentCardById(Long id);
}
