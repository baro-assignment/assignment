package com.example.assignment.global.auth.security.handler;

import com.example.assignment.global.dto.response.ErrorResponse;
import com.example.assignment.global.exception.ExceptionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * 인가(권한)이 부족한 사용자가 보호된 리소스에 접근하려 할 때 실행되는 Handler의 구현체입니다.
 * 403 Forbidden 상태 코드와 함께 접근 거부 메시지를 client에 전달합니다.
 */
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        ExceptionType exceptionType = ExceptionType.ACCESS_DENIED;
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(exceptionType.getHttpStatus().value());
        response.getWriter().write(objectMapper.writeValueAsString(
                ErrorResponse.of(exceptionType, exceptionType.getMessage())
        ));
    }
}
