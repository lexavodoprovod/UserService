package com.innowise.UserService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleNotFound(EntityNotFoundException e) {
        HttpStatus notFound = HttpStatus.NOT_FOUND;
        ErrorDetails exception = ErrorDetails.builder()
                .message(e.getMessage())
                .errorName(notFound.getReasonPhrase())
                .httpStatus(notFound.value())
                .timestamp(LocalDateTime.now())
                .build();
        return  new ResponseEntity<>(exception, notFound);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorDetails> handleConflict(BusinessException e) {
        HttpStatus conflict = HttpStatus.CONFLICT;
        ErrorDetails exception = ErrorDetails.builder()
                .message(e.getMessage())
                .errorName(conflict.getReasonPhrase())
                .httpStatus(conflict.value())
                .timestamp(LocalDateTime.now())
                .build();
        return  new ResponseEntity<>(exception, conflict);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult()
                .getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        return  ResponseEntity.badRequest().body(errors);
    }


}
