package com.example.assignment.global.auth.security.filter;

import com.example.assignment.domain.user.enums.UserRole;
import com.example.assignment.global.auth.jwt.JwtAuthenticationToken;
import com.example.assignment.global.auth.jwt.JwtUtil;
import com.example.assignment.global.dto.AuthInfo;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null) {
            String token = jwtUtil.extractTokenFromBearerHeader(authHeader);
            Claims claims = jwtUtil.extractClaims(token);
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                setAuthentication(claims);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(Claims claims) {
        String username = claims.getSubject();
        String nickname = claims.get("nickname", String.class);
        UserRole userRole = UserRole.valueOf(claims.get("userRole", String.class));

        AuthInfo authInfo = new AuthInfo(username, nickname, userRole);
        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(authInfo);
        SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationToken);
    }
}
