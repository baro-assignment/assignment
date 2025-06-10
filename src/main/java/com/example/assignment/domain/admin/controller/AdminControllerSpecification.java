package com.example.assignment.domain.admin.controller;

import com.example.assignment.domain.admin.dto.response.GrantAdminRoleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Admin", description = "관리자 전용 API")
public interface AdminControllerSpecification {

    @Operation(summary = "관리자 권한 부여",
    description = "사용자 id를 받아 해당 사용자에게 관리자 권한을 부여합니다.")
    ResponseEntity<GrantAdminRoleResponse> grantAdminRole(Long userId);

}
