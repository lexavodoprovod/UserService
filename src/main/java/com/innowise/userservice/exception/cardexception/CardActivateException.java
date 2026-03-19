package com.innowise.userservice.exception.cardexception;

import com.innowise.userservice.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class CardActivateException extends BusinessException {
    public CardActivateException(Long id) {
        super("Cannot activate payment cards of user with id[%s]".formatted(id), HttpStatus.CONFLICT);
    }
}
