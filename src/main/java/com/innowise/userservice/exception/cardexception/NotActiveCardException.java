package com.innowise.userservice.exception.cardexception;

import com.innowise.userservice.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class NotActiveCardException extends BusinessException {
    public NotActiveCardException(Long id) {
        super("PaymentCard with id [%s] is not active".formatted(id), HttpStatus.BAD_REQUEST);
    }
}
