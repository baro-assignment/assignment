package com.example.assignment.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ExceptionType exceptionType;

    public CustomException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }

    public CustomException(ExceptionType exceptionType, String message) {
        super(message);
        this.exceptionType = exceptionType;
    }
}
