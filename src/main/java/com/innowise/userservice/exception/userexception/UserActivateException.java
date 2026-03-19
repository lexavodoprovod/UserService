package com.innowise.userservice.exception.userexception;

import com.innowise.userservice.exception.BusinessException;

public class UserActivateException extends BusinessException {
    public UserActivateException(Long id) {
        super("Cannot activate user with id[%s]".formatted(id));
    }
}
