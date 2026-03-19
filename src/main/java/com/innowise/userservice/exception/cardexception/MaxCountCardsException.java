package com.innowise.userservice.exception.cardexception;

import com.innowise.userservice.exception.BusinessException;

public class MaxCountCardsException extends BusinessException {
    public MaxCountCardsException(int countOfCards) {
        super("User cannot have more than %s active paymentCards".formatted(countOfCards));
    }
}
