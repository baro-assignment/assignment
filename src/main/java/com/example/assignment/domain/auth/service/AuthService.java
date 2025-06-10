package com.example.assignment.domain.auth.service;

import com.example.assignment.domain.auth.dto.request.SignUpRequest;
import com.example.assignment.domain.auth.dto.response.SignUpResponse;
import com.example.assignment.domain.user.entity.User;
import com.example.assignment.domain.user.enums.UserRole;
import com.example.assignment.domain.user.repository.UserRepository;
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

    public SignUpResponse singUp(SignUpRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(ExceptionType.USER_ALREADY_EXISTS);
        }

        User user = User.builder()
                .username(request.getUsername())
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .userRole(UserRole.USER)
                .build();

        User savedUser = userRepository.save(user);
        return SignUpResponse.from(savedUser);
    }
}
