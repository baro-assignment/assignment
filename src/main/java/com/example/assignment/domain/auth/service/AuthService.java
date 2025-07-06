package com.example.assignment.domain.auth.service;

import com.example.assignment.domain.auth.dto.request.LoginRequest;
import com.example.assignment.domain.auth.dto.request.SignUpRequest;
import com.example.assignment.domain.auth.dto.response.LoginResponse;
import com.example.assignment.domain.auth.dto.response.SignUpResponse;
import com.example.assignment.domain.user.entity.User;
import com.example.assignment.domain.user.enums.UserRole;
import com.example.assignment.domain.user.repository.UserRepository;
import com.example.assignment.global.auth.jwt.JwtUtil;
import com.example.assignment.global.exception.CustomException;
import com.example.assignment.global.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public SignUpResponse signUpUser(SignUpRequest request) {
        return signUp(request, UserRole.USER);
    }

    public SignUpResponse signUpAdmin(SignUpRequest request) {
        return signUp(request, UserRole.ADMIN);
    }

    private SignUpResponse signUp(SignUpRequest request, UserRole userRole) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ExceptionType.USER_ALREADY_EXISTS);
        }

        User user = User.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .userRole(userRole)
                .build();

        User savedUser = userRepository.save(user);
        return SignUpResponse.from(savedUser);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ExceptionType.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ExceptionType.INVALID_CREDENTIALS);
        }

        String token = jwtUtil.createBearerToken(user.getId(), user.getEmail(), user.getNickname(), user.getUserRole());
        return new LoginResponse(token);
    }
}
