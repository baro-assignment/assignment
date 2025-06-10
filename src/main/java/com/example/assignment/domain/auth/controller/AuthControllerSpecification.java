package com.example.assignment.domain.auth.controller;

import com.example.assignment.domain.auth.dto.request.LoginRequest;
import com.example.assignment.domain.auth.dto.request.SignUpRequest;
import com.example.assignment.domain.auth.dto.response.LoginResponse;
import com.example.assignment.domain.auth.dto.response.SignUpResponse;
import com.example.assignment.global.annotation.ApiErrorResponses;
import com.example.assignment.global.exception.ExceptionType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Authorization", description = "인증 관련 API")
public interface AuthControllerSpecification {

    @Operation(summary = "회원가입",
    description = "사용자 정보를 받아 회원가입을 수행합니다.")
    @ApiErrorResponses(value = {ExceptionType.USER_ALREADY_EXISTS})
    ResponseEntity<SignUpResponse> signUp(SignUpRequest signUpRequest);

    @Operation(summary = "로그인",
    description = "사용자 인증 정보를 받아 로그인을 수행합니다.")
    @ApiErrorResponses(value = {ExceptionType.INVALID_CREDENTIALS})
    ResponseEntity<LoginResponse> login(LoginRequest loginRequest);
}
