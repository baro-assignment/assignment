package com.example.assignment.domain.user.service;

import com.example.assignment.domain.user.dto.response.UserProfileResponse;
import com.example.assignment.domain.user.entity.User;
import com.example.assignment.domain.user.repository.UserRepository;
import com.example.assignment.global.dto.AuthInfo;
import com.example.assignment.global.exception.CustomException;
import com.example.assignment.global.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserProfileResponse getMyProfile(AuthInfo authInfo) {
        User user = userRepository.findByEmail(authInfo.getEmail())
                .orElseThrow(() -> new CustomException(ExceptionType.USER_NOT_FOUND));

        return UserProfileResponse.of(user);
    }
}
