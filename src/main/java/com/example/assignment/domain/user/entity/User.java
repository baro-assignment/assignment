package com.example.assignment.domain.user.entity;

import com.example.assignment.domain.user.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
public class User {

    private Long id;
    private String username;
    private String password;
    private String nickname;
    private UserRole userRole;

    @Builder
    public User(Long id, String username, String password, String nickname, UserRole userRole) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.userRole = userRole;
    }
}
