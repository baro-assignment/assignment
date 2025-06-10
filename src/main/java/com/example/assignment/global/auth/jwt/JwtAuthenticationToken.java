package com.example.assignment.global.auth.jwt;

import com.example.assignment.global.dto.AuthInfo;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final AuthInfo authInfo;

    public JwtAuthenticationToken(AuthInfo authInfo) {
        super(authInfo.getAuthorities());
        this.authInfo = authInfo;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return authInfo;
    }
}
