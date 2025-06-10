package com.example.assignment.global.dto;

import com.example.assignment.domain.user.enums.UserRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

@Getter
public class AuthInfo {

    private String username;
    private String nickname;
    private UserRole userRole;
    private final Collection<? extends GrantedAuthority> authorities;

    public AuthInfo(String username, String nickname, UserRole userRole) {
        this.username = username;
        this.nickname = nickname;
        this.userRole = userRole;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(userRole.toRoleName()));
    }
}
