package com.example.assignment.domain.admin.controller;

import com.example.assignment.domain.admin.dto.response.GrantAdminRoleResponse;
import com.example.assignment.domain.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController implements AdminControllerSpecification{

    private final AdminService adminService;

    @PatchMapping("/users/{userId}/roles")
    public ResponseEntity<GrantAdminRoleResponse> grantAdminRole(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok()
                .body(adminService.grantAdminRoleToUser(userId));
    }

    @GetMapping("/check")
    public ResponseEntity<String> checkAdminRole() {
        return ResponseEntity.ok()
                .body("OK");
    }
}
