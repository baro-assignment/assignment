package com.example.assignment.domain.user.controller;

import com.example.assignment.domain.user.dto.response.UserProfileResponse;
import com.example.assignment.domain.user.service.UserService;
import com.example.assignment.global.dto.AuthInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserControllerSpecification {

    private final UserService userService;

    @GetMapping("/users/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(
            @AuthenticationPrincipal AuthInfo authInfo
    ) {
        return ResponseEntity.ok()
                .body(userService.getMyProfile(authInfo));
    }
}
