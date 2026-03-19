package com.innowise.userservice.exception.cardexception;

import com.innowise.userservice.exception.BusinessException;

public class CardDeactivateException extends BusinessException {
    public CardDeactivateException(Long id) {
        super("Cannot deactivate payment cards of user with id[%s]".formatted(id));
    }

    public CardDeactivateException(){
        super("Cannot deactivate payment cards of user");
    }
    }
