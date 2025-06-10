package com.example.assignment.global.auth.security.filter;

import com.example.assignment.global.dto.response.ErrorResponse;
import com.example.assignment.global.exception.CustomException;
import com.example.assignment.global.exception.ExceptionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


/**
 * JWT 인증 과정에서 발생하는 예외를 처리하는 필터.
 * SecurityFilterChain 상에서 JwtAuthenticationFilter 이전에 위치하여,
 * 발생 가능한 인증 예외를 캐치하고 일관된 JSON 응답을 반환합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (CustomException e) {
            log.error("[CUSTOM_EXCEPTION] statusCode = {}, msg = {}",
                    e.getExceptionType().getHttpStatus(), e.getMessage());
            setErrorResponse(response, e.getExceptionType(), e.getMessage());
        } catch (Exception e) {
            log.error("[UNEXPECTED_EXCEPTION] {}", e.getMessage(), e);
            ExceptionType exceptionType = ExceptionType.INTERNAL_SERVER_ERROR;
            setErrorResponse(response, exceptionType, exceptionType.getMessage());
        }
    }

    private void setErrorResponse(
            HttpServletResponse response,
            ExceptionType exceptionType,
            String message
    ) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(exceptionType.getHttpStatus().value());
        response.getWriter().write(objectMapper.writeValueAsString(
                ErrorResponse.of(exceptionType, message)
        ));
    }
}
