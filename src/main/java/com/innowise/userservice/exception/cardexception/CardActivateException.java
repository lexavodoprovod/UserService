package com.innowise.userservice.exception.cardexception;

import com.innowise.userservice.exception.BusinessException;

public class CardActivateException extends BusinessException {
    public CardActivateException(Long id) {
        super("Cannot activate payment cards of user with id[%s]".formatted(id));
    }
}
