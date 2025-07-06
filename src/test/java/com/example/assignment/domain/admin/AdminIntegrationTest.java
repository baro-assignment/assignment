package com.example.assignment.domain.admin;

import com.example.assignment.domain.admin.dto.response.GrantAdminRoleResponse;
import com.example.assignment.domain.auth.dto.request.LoginRequest;
import com.example.assignment.domain.auth.dto.request.SignUpRequest;
import com.example.assignment.domain.user.entity.User;
import com.example.assignment.domain.user.enums.UserRole;
import com.example.assignment.domain.user.repository.UserRepository;
import com.example.assignment.global.auth.jwt.JwtAuthenticationToken;
import com.example.assignment.global.dto.AuthInfo;
import com.example.assignment.global.exception.ExceptionType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private JwtAuthenticationToken userAuthenticationToken;
    private JwtAuthenticationToken adminAuthenticationToken;

    private User user;
    private User adminUser;

    @BeforeEach
    public void setUp() {
        AuthInfo userAuthInfo = new AuthInfo(1L,"user@test.com", "일반 사용자", UserRole.USER);
        userAuthenticationToken = new JwtAuthenticationToken(userAuthInfo);
        AuthInfo adminAuthInfo = new AuthInfo(2L, "admin@test.com", "관리자", UserRole.ADMIN);
        adminAuthenticationToken = new JwtAuthenticationToken(adminAuthInfo);

        user = new User(userAuthInfo);
        adminUser = new User(adminAuthInfo);

        userRepository.save(user);
        userRepository.save(adminUser);
    }

    @Nested
    @DisplayName("관리자 권한 부여")
    class AdminGrant {

        @Test
        @DisplayName("관리자 권한 부여에 성공하여 200 응답을 리턴한다.")
        void grant() throws Exception {
            mockMvc.perform(patch("/admin/users/{userId}/grant", user.getId())
                            .with(authentication(adminAuthenticationToken))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").exists())
                    .andExpect(jsonPath("$.nickname").exists())
                    .andExpect(jsonPath("$.role").exists());
        }

        @Test
        @DisplayName("일반사용자가 권한을 부여하려고 하면 401 에러를 전달한다.")
        void forbiddenRequest() throws Exception {
            mockMvc.perform(patch("/admin/users/{userId}/grant", user.getId())
                            .with(authentication(userAuthenticationToken))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error").exists())
                    .andExpect(jsonPath("$.error.code").value(ExceptionType.ACCESS_DENIED.name()))
                    .andExpect(jsonPath("$.error.message").value(ExceptionType.ACCESS_DENIED.getMessage()));
        }

        @Test
        @DisplayName("존재하지 않는 사용자에게 권한을 부여하려고 하면 NOT_FOUND 에러를 전달한다.")
        void userNotFound() throws Exception {
            mockMvc.perform(patch("/admin/users/{userId}/grant", 99L)
                            .with(authentication(adminAuthenticationToken))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").exists())
                    .andExpect(jsonPath("$.error.code").value(ExceptionType.USER_NOT_FOUND.name()))
                    .andExpect(jsonPath("$.error.message").value(ExceptionType.USER_NOT_FOUND.getMessage()));
        }
    }
}