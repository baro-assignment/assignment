package com.example.assignment.global.dto.response;

import com.example.assignment.global.exception.ExceptionType;
import lombok.Getter;

@Getter
public class ErrorResponse {

    private ErrorDetail error;

    private ErrorResponse(ErrorDetail error) {
        this.error = error;
    }

    public static ErrorResponse of(ExceptionType exceptionType, String message) {
        return new ErrorResponse(
                new ErrorDetail(
                        exceptionType.name(),
                        message
                )
        );
    }

    @Getter
    public static class ErrorDetail {

        private String code;
        private String message;

        public ErrorDetail(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
