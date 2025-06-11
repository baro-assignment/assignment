package com.example.assignment.global.auth;

import com.example.assignment.domain.user.enums.UserRole;
import com.example.assignment.global.auth.jwt.JwtUtil;
import com.example.assignment.global.auth.security.config.SecurityConfig;
import com.example.assignment.global.auth.security.handler.CustomAccessDeniedHandler;
import com.example.assignment.global.auth.security.handler.CustomAuthenticationEntryPoint;
import com.example.assignment.global.exception.ExceptionType;
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

@SpringBootTest
@AutoConfigureMockMvc
@Import({SecurityConfig.class, JwtUtil.class, CustomAuthenticationEntryPoint.class, CustomAccessDeniedHandler.class})
public class AuthSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    private String userToken() {
        return jwtUtil.createBearerToken("user", "일반 사용자", UserRole.USER);
    }

    private String adminToken() {
        return jwtUtil.createBearerToken("admin", "관리자", UserRole.ADMIN);
    }

    @Test
    void 인증없이_일반API에_접근시_401에러_리턴() throws Exception {
        mockMvc.perform(get("/auth/check"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.code").value(ExceptionType.AUTHENTICATION_REQUIRED.name()))
                .andExpect(jsonPath("$.error.message").value(ExceptionType.AUTHENTICATION_REQUIRED.getMessage()));
    }

    @Nested
    class 일반사용자 {
        @Test
        void 일반API에_접근시_200응답_리턴() throws Exception {
            mockMvc.perform(get("/auth/check")
                            .header("Authorization", "Bearer " + userToken()))
                    .andExpect(status().isOk());
        }

        @Test
        void 관리자API에_접근시_403에러_리턴() throws Exception {
            mockMvc.perform(get("/admin/check")
                            .header("Authorization", "Bearer " + userToken()))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error").exists())
                    .andExpect(jsonPath("$.error.code").value(ExceptionType.ACCESS_DENIED.name()))
                    .andExpect(jsonPath("$.error.message").value(ExceptionType.ACCESS_DENIED.getMessage()));
        }
    }

    @Nested
    class 관리자 {
        @Test
        void 일반API에_접근시_200응답_리턴() throws Exception {
            mockMvc.perform(get("/auth/check")
                            .header("Authorization", "Bearer " + adminToken()))
                    .andExpect(status().isOk());
        }

        @Test
        void 관리자API에_접근시_200응답_리턴() throws Exception {
            mockMvc.perform(get("/admin/check")
                            .header("Authorization", "Bearer " + adminToken()))
                    .andExpect(status().isOk());
        }
    }
}
