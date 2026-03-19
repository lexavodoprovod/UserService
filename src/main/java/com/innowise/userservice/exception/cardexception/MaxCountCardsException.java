package com.innowise.userservice.exception.cardexception;

import com.innowise.userservice.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class MaxCountCardsException extends BusinessException {
    public MaxCountCardsException(int countOfCards) {
        super("User cannot have more than %s active paymentCards".formatted(countOfCards), HttpStatus.BAD_REQUEST);
    }
}
