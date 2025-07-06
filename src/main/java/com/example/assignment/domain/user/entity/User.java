package com.example.assignment.domain.user.entity;

import com.example.assignment.domain.user.enums.UserRole;
import com.example.assignment.global.dto.AuthInfo;
import lombok.Builder;
import lombok.Getter;

@Getter
public class User {

    private Long id;
    private String email;
    private String password;
    private String nickname;
    private UserRole userRole;

    @Builder
    public User(Long id, String email, String password, String nickname, UserRole userRole) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.userRole = userRole;
    }

    public User(AuthInfo authInfo) {
        this.id = authInfo.getId();
        this.email = authInfo.getEmail();
        this.nickname = authInfo.getNickname();
        this.userRole = authInfo.getUserRole();
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }
}
