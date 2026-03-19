package com.innowise.userservice.exception.cardexception;

import com.innowise.userservice.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class CardDeactivateException extends BusinessException {
    public CardDeactivateException(Long id) {
        super("Cannot deactivate payment cards of user with id[%s]".formatted(id), HttpStatus.CONFLICT);
    }

    public CardDeactivateException(){
        super("Cannot deactivate payment cards of user", HttpStatus.CONFLICT);
    }
    }
