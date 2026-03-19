package com.innowise.userservice.exception.userexception;

import com.innowise.userservice.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class UserDeactivateException extends BusinessException {
    public UserDeactivateException(Long id) {
        super("Cannot deactivate user with id[%s]".formatted(id), HttpStatus.CONFLICT);
    }
}
