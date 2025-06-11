package com.example.assignment.domain.admin.controller;

import com.example.assignment.domain.admin.dto.response.GrantAdminRoleResponse;
import com.example.assignment.domain.user.dto.response.UserProfileResponse;
import com.example.assignment.global.annotation.ApiErrorResponses;
import com.example.assignment.global.exception.ExceptionType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Admin", description = "관리자 전용 API")
public interface AdminControllerSpecification {

    @Operation(summary = "관리자 권한 부여",
    description = "사용자 id를 받아 해당 사용자에게 관리자 권한을 부여합니다.")
    @ApiErrorResponses(value = {ExceptionType.AUTHENTICATION_REQUIRED, ExceptionType.ACCESS_DENIED, ExceptionType.USER_NOT_FOUND})
    ResponseEntity<GrantAdminRoleResponse> grantAdminRole(Long userId);

    @Operation(summary = "모든 사용자의 프로필 조회",
            description = "현재 회원가입된 모든 사용자의 프로필을 조회합니다.")
    @ApiErrorResponses(value = {ExceptionType.AUTHENTICATION_REQUIRED, ExceptionType.ACCESS_DENIED})
    ResponseEntity<List<UserProfileResponse>> getAllUserProfiles();
}
