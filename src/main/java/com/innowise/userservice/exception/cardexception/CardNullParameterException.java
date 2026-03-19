package com.innowise.userservice.exception.cardexception;

import com.innowise.userservice.exception.BusinessException;

public class CardNullParameterException extends BusinessException {

    public CardNullParameterException() {
        super("Try to use null parameter in PaymentCardService");
    }
}
