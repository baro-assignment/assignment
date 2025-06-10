package com.example.assignment.domain.auth.controller;

import com.example.assignment.domain.auth.dto.request.SignUpRequest;
import com.example.assignment.domain.auth.dto.response.SignUpResponse;
import com.example.assignment.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthControllerSpecification {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody @Valid SignUpRequest request) {
        return ResponseEntity.ok()
                .body(authService.singUp(request));
    }
}
