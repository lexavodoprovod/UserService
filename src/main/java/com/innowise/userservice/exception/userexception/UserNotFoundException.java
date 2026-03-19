package com.innowise.userservice.exception.userexception;

import com.innowise.userservice.exception.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException(Long id){
        super("Could not find user with id[%s]".formatted(id));
    }
}
