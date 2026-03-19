package com.innowise.userservice.exception.cardexception;

import com.innowise.userservice.exception.BusinessException;

public class NotActiveCardException extends BusinessException {
    public NotActiveCardException(Long id) {
        super("PaymentCard with id [%s] is not active".formatted(id));
    }
}
