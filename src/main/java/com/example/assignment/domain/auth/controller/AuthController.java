package com.example.assignment.domain.auth.controller;

import com.example.assignment.domain.auth.dto.request.LoginRequest;
import com.example.assignment.domain.auth.dto.request.SignUpRequest;
import com.example.assignment.domain.auth.dto.response.LoginResponse;
import com.example.assignment.domain.auth.dto.response.SignUpResponse;
import com.example.assignment.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthControllerSpecification {

    private final AuthService authService;

    @PostMapping("/signup/user")
    public ResponseEntity<SignUpResponse> signUpUser(@RequestBody @Valid SignUpRequest request) {
        return ResponseEntity.ok()
                .body(authService.signUpUser(request));
    }

    @PostMapping("/signup/admin")
    public ResponseEntity<SignUpResponse> signUpAdmin(@RequestBody @Valid SignUpRequest request) {
        return ResponseEntity.ok()
                .body(authService.signUpAdmin(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        return ResponseEntity.ok()
                .body(authService.login(loginRequest));
    }

    @GetMapping("/auth/check")
    public ResponseEntity<Void> check() {
        return ResponseEntity.ok().build();
    }
}
