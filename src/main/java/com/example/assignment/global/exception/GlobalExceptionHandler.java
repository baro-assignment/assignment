package com.example.assignment.global.exception;

import com.example.assignment.global.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 도메인 커스텀 예외 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.error("[CUSTOM_EXCEPTION] statusCode = {}, msg = {}",
                e.getExceptionType().getHttpStatus(), e.getMessage());

        return ResponseEntity.status(e.getExceptionType().getHttpStatus())
                .body(ErrorResponse.of(e.getExceptionType(), e.getMessage()));
    }

    // @Valid 검증 실패 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ExceptionType exceptionType = ExceptionType.REQUEST_VALIDATION_FAILED;

        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> "[" + error.getField() + "] " + error.getDefaultMessage())
                .toList();

        log.warn("[VALIDATION ERROR] {}", errors);

        return ResponseEntity.status(exceptionType.getHttpStatus())
                .body(ErrorResponse.of(exceptionType, String.join(", ", errors)));
    }

    // 기타 예상치 못한 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("[UNEXPECTED_EXCEPTION] {}", ex.getMessage(), ex);

        ExceptionType exceptionType = ExceptionType.INTERNAL_SERVER_ERROR;

        return ResponseEntity.status(exceptionType.getHttpStatus())
                .body(ErrorResponse.of(exceptionType, exceptionType.getMessage()));
    }
}
