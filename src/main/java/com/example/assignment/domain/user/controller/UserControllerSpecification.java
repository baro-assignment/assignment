package com.example.assignment.domain.user.controller;

import com.example.assignment.domain.user.dto.response.UserProfileResponse;
import com.example.assignment.global.annotation.ApiErrorResponses;
import com.example.assignment.global.dto.AuthInfo;
import com.example.assignment.global.exception.ExceptionType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "User", description = "사용자 관련 API")
public interface UserControllerSpecification {

    @Operation(summary = "본인 프로필 조회",
    description = "현재 로그인 중인 사용자의 프로필을 조회합니다.")
    @ApiErrorResponses(value = {ExceptionType.AUTHENTICATION_REQUIRED, ExceptionType.USER_NOT_FOUND})
    ResponseEntity<UserProfileResponse> getMyProfile(AuthInfo authInfo);
}
