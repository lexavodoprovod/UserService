package com.innowise.UserService.exception;

import lombok.Builder;

import java.time.LocalDateTime;


@Builder
public class ErrorDetails {

    private final String message;

    private final String errorName;

    private final int httpStatus;

    private final LocalDateTime timestamp;

    private final String path;
}
