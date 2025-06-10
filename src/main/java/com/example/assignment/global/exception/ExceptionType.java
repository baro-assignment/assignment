package com.example.assignment.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionType {

    // Auth
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 가입된 사용자입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."),
    AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "접근을 위해선 인증이 필요합니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),

    // Token
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 인증 토큰입니다."),

    // Common
    REQUEST_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "요청 데이터가 유효하지 않습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부에 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ExceptionType(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
