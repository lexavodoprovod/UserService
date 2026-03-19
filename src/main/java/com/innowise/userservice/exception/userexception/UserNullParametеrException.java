package com.innowise.userservice.exception.userexception;

import com.innowise.userservice.exception.BusinessException;

public class UserNullParametеrException extends BusinessException {
    public UserNullParametеrException() {
        super("Try to use null parameter in UserService");
    }
}
