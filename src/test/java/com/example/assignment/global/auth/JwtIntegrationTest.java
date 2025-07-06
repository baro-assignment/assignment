package com.example.assignment.global.auth;

import com.example.assignment.domain.user.enums.UserRole;
import com.example.assignment.global.auth.jwt.JwtUtil;
import com.example.assignment.global.auth.security.config.SecurityConfig;
import com.example.assignment.global.auth.security.handler.CustomAccessDeniedHandler;
import com.example.assignment.global.auth.security.handler.CustomAuthenticationEntryPoint;
import com.example.assignment.global.exception.ExceptionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Spring Security 환경에서 JWT 기반 인증이 올바르게 작동하는지 통합적으로 검증하는 테스트
 *
 * 주요 검증 내용:
 * 1. 유효한 JWT로 접근
 * 2. Authorization 헤더가 없는 경우
 * 3. 만료된 JWT로 접근
 * 4. 조작되거나 잘못된 JWT로 접근
 * 5. 잘못된 형식(Bearer prefix 누락 등)으로 토큰이 전달된 경우
 */

@SpringBootTest
@AutoConfigureMockMvc
@Import({SecurityConfig.class, JwtUtil.class, CustomAuthenticationEntryPoint.class, CustomAccessDeniedHandler.class})
public class JwtIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    // 이미 만료된 토큰
    private String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTVUJJTjA2MjAiLCJuaWNrbmFtZSI6IuyImOu5iCIsInVzZXJSb2xlIjoiVVNFUiIsImV4cCI6MTc0OTU1ODg4OCwiaWF0IjoxNzQ5NTU1Mjg4fQ.ot4-G87nYHeOmEQf5v1QweUiO7VYY4ubiF2PSH6Kueg";

    private String createdValidToken() {
        return jwtUtil.createBearerToken(1L,"testUser@gmail.com", "nickname", UserRole.USER);
    }

    @Nested
    @DisplayName("JWT 인증이 필요한 API 접근")
    class jwtTest {

        @Test
        @DisplayName("유효한 토큰으로 접근한다면 200 응답을 리턴한다.")
        void 유효한_토큰으로_접근한다면_200응답_리턴() throws Exception {
            String token = createdValidToken();

            mockMvc.perform(get("/auth/check")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("인증 헤더 없이 접근한다면 AUTHENTICATION_REQUIRED 401 에러를 전달한다. ")
        void Authorization헤더가_없다면_401응답_리턴() throws Exception {
            mockMvc.perform(get("/auth/check"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error").exists())
                    .andExpect(jsonPath("$.error.code").value(ExceptionType.AUTHENTICATION_REQUIRED.name()))
                    .andExpect(jsonPath("$.error.message").value(ExceptionType.AUTHENTICATION_REQUIRED.getMessage()));
        }

        @Test
        @DisplayName("만료된 토큰으로 접근한다면 EXPIRED_TOKEN 401 에러를 전달한다.")
        void 만료된_토큰으로_접근한다면_401응답_리턴() throws Exception {
            String token = expiredToken;

            mockMvc.perform(get("/auth/check")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error").exists())
                    .andExpect(jsonPath("$.error.code").value(ExceptionType.EXPIRED_TOKEN.name()))
                    .andExpect(jsonPath("$.error.message").value(ExceptionType.EXPIRED_TOKEN.getMessage()));
        }

        @Test
        @DisplayName("조작된 토큰으로 접근한다면 INVALID_TOKEN 401 에러를 전달한다. ")
        void 잘못된_토큰으로_접근한다면_401응답_리턴() throws Exception {
            String token = "invalidToken";

            mockMvc.perform(get("/auth/check")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error").exists())
                    .andExpect(jsonPath("$.error.code").value(ExceptionType.INVALID_TOKEN.name()));
        }

        @Test
        @DisplayName("잘못된 형식의 토큰으로 접근한다면 INVALID_TOKEN 401 에러를 전달한다.")
        void 잘못된_형식의_토큰으로_접근한다면_401응답_리턴() throws Exception {
            String token = createdValidToken();

            // Bearer prefix 없이 접근
            mockMvc.perform(get("/auth/check")
                            .header("Authorization", token))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error").exists())
                    .andExpect(jsonPath("$.error.code").value(ExceptionType.INVALID_TOKEN.name()))
                    .andExpect(jsonPath("$.error.message").value("잘못된 형식의 토큰입니다."));
        }
    }
}
