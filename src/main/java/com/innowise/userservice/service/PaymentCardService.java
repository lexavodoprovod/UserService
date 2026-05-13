package com.innowise.userservice.service;

import com.innowise.userservice.dto.PaymentCardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentCardService {
    PaymentCardDto createPaymentCard(PaymentCardDto paymentCardDto);

    PaymentCardDto getPaymentCardById(Long id);

    Page<PaymentCardDto> getAllPaymentCardsByUserId(Long id, String number, Pageable pageable);

    List<PaymentCardDto> getAllActiveCardsByUserId(Long id);

    Page<PaymentCardDto> getAllPaymentCards(String number, Pageable pageable);

    PaymentCardDto updatePaymentCard(PaymentCardDto paymentCardDto);

    boolean activatePaymentCardById(Long id);

    boolean deactivatePaymentCardById(Long id);
}
