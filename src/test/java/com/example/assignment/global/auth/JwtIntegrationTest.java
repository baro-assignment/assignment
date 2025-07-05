package com.example.assignment.global.auth;

import com.example.assignment.domain.user.enums.UserRole;
import com.example.assignment.global.auth.jwt.JwtUtil;
import com.example.assignment.global.auth.security.config.SecurityConfig;
import com.example.assignment.global.auth.security.handler.CustomAccessDeniedHandler;
import com.example.assignment.global.auth.security.handler.CustomAuthenticationEntryPoint;
import com.example.assignment.global.exception.ExceptionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import({SecurityConfig.class, JwtUtil.class, CustomAuthenticationEntryPoint.class, CustomAccessDeniedHandler.class})
public class JwtIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    private String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTVUJJTjA2MjAiLCJuaWNrbmFtZSI6IuyImOu5iCIsInVzZXJSb2xlIjoiVVNFUiIsImV4cCI6MTc0OTU1ODg4OCwiaWF0IjoxNzQ5NTU1Mjg4fQ.ot4-G87nYHeOmEQf5v1QweUiO7VYY4ubiF2PSH6Kueg";
    private String invalidToken = "invalidToken";

    private String createdValidToken() {
        return jwtUtil.createBearerToken("username", "nickname", UserRole.USER);
    }

    @Test
    void 유효한_토큰으로_접근한다면_200응답_리턴() throws Exception {
        String token = createdValidToken();

        mockMvc.perform(get("/auth/check")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void Authorization헤더가_없다면_401응답_리턴() throws Exception {
        mockMvc.perform(get("/auth/check"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.code").value(ExceptionType.AUTHENTICATION_REQUIRED.name()))
                .andExpect(jsonPath("$.error.message").value(ExceptionType.AUTHENTICATION_REQUIRED.getMessage()));
    }

    @Test
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
    void 잘못된_토큰으로_접근한다면_401응답_리턴() throws Exception {
        String token = invalidToken;

        mockMvc.perform(get("/auth/check")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.code").value(ExceptionType.INVALID_TOKEN.name()));
    }

    @Test
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
