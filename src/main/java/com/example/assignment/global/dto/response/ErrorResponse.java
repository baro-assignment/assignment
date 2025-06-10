package com.example.assignment.global.dto.response;

import com.example.assignment.global.exception.ExceptionType;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "에러 정보")
    public static class ErrorDetail {

        @Schema(description = "에러 코드")
        private String code;
        @Schema(description = "에러 메시지")
        private String message;

        public ErrorDetail(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
