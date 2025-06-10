package com.example.assignment.domain.admin.service;

import com.example.assignment.domain.admin.dto.response.GrantAdminRoleResponse;
import com.example.assignment.domain.user.entity.User;
import com.example.assignment.domain.user.enums.UserRole;
import com.example.assignment.domain.user.repository.UserRepository;
import com.example.assignment.global.exception.CustomException;
import com.example.assignment.global.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public GrantAdminRoleResponse grantAdminRoleToUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionType.USER_NOT_FOUND));

        user.setUserRole(UserRole.ADMIN);
        userRepository.save(user);
        return GrantAdminRoleResponse.from(user);
    }
}
