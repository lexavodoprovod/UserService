package com.innowise.userservice.exception.cardexception;

import com.innowise.userservice.exception.EntityNotFoundException;

public class CardNotFoundException extends EntityNotFoundException {
    public CardNotFoundException(Long id){
        super("Could not find card with id[%s] ".formatted(id));
    }

    public CardNotFoundException(String number){
        super("Could not find card with number[%s]".formatted(number));
    }
}
