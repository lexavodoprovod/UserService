package com.innowise.userservice.exception.userexception;

import com.innowise.userservice.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class UserNullParameterException extends BusinessException {
    public UserNullParameterException() {
        super("Try to use null parameter in UserService", HttpStatus.BAD_REQUEST);
    }
}
