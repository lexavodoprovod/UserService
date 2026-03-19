package com.innowise.userservice.exception.userexception;

import com.innowise.userservice.exception.BusinessException;

public class NotActiveUserException extends BusinessException {
    public NotActiveUserException(Long id) {
        super("User with id [%s] is not active".formatted(id));
    }
}
