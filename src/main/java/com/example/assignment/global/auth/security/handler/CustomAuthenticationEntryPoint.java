package com.example.assignment.global.auth.security.handler;

import com.example.assignment.global.dto.response.ErrorResponse;
import com.example.assignment.global.exception.ExceptionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 인증되지 않은 사용자가 보호된 리소스에 접근할 경우 실행되는 EntryPoint 구현체.
 * 주로 Authorization 헤더가 없어 JWT 인증을 수행하지 않은 경우에 해당합니다.
 * 401 Unauthorized 상태 코드와 함께 인증 필요 메시지를 client에 전달합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        log.warn("[AUTHENTICATION_REQUIRED] URI: {}", request.getRequestURI());

        ExceptionType exceptionType = ExceptionType.AUTHENTICATION_REQUIRED;
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(exceptionType.getHttpStatus().value());
        response.getWriter().write(objectMapper.writeValueAsString(
                ErrorResponse.of(exceptionType, exceptionType.getMessage())
        ));
    }
}
