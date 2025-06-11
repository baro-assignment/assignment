package com.example.assignment.domain.admin.controller;

import com.example.assignment.domain.admin.dto.response.GrantAdminRoleResponse;
import com.example.assignment.domain.admin.service.AdminService;
import com.example.assignment.domain.user.dto.response.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController implements AdminControllerSpecification{

    private final AdminService adminService;

    @PatchMapping("/users/{userId}/grant")
    public ResponseEntity<GrantAdminRoleResponse> grantAdminRole(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok()
                .body(adminService.grantAdminRoleToUser(userId));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserProfileResponse>> getAllUserProfiles() {
        return ResponseEntity.ok()
                .body(adminService.getAllUserProfiles());
    }
}
