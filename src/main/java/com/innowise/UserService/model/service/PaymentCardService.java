package com.innowise.UserService.model.service;

import com.innowise.UserService.model.dto.PaymentCardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentCardService {
    PaymentCardDto createPaymentCard(PaymentCardDto paymentCardDto);

    PaymentCardDto getPaymentCardById(Long id);

    List<PaymentCardDto> getPaymentCardsByUserId(Long id);

    Page<PaymentCardDto> getPaymentCardsByUserId(String number, Pageable pageable);

    PaymentCardDto updatePaymentCard(PaymentCardDto paymentCardDto);

    boolean activatePaymentCardById(Long id);

    boolean deactivatePaymentCardById(Long id);
}
