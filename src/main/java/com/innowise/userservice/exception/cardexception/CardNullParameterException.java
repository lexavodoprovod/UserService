package com.innowise.userservice.exception.cardexception;

import com.innowise.userservice.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class CardNullParameterException extends BusinessException {

    public CardNullParameterException() {
        super("Try to use null parameter in PaymentCardService", HttpStatus.BAD_REQUEST);
    }
}
