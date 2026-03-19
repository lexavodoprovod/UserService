package com.innowise.userservice.exception.cardexception;

import com.innowise.userservice.exception.BusinessException;

public class ExistCardException extends BusinessException {
    public ExistCardException(String number) {
        super("Card with number [%s] already exists!".formatted(number));
    }
}
