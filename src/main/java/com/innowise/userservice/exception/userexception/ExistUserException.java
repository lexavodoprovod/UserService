package com.innowise.userservice.exception.userexception;

import com.innowise.userservice.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class ExistUserException extends BusinessException {
    public ExistUserException(String email) {
        super("User with email [%s] already exists!".formatted(email), HttpStatus.CONFLICT);
    }
}
