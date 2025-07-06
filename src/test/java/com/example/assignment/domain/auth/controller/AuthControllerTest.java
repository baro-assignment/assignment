package com.example.assignment.domain.auth.controller;

import com.example.assignment.domain.auth.dto.request.LoginRequest;
import com.example.assignment.domain.auth.dto.request.SignUpRequest;
import com.example.assignment.domain.auth.dto.response.LoginResponse;
import com.example.assignment.domain.auth.dto.response.SignUpResponse;
import com.example.assignment.domain.auth.service.AuthService;
import com.example.assignment.domain.user.entity.User;
import com.example.assignment.domain.user.enums.UserRole;
import com.example.assignment.global.auth.jwt.JwtUtil;
import com.example.assignment.global.auth.security.config.SecurityConfig;
import com.example.assignment.global.auth.security.handler.CustomAccessDeniedHandler;
import com.example.assignment.global.auth.security.handler.CustomAuthenticationEntryPoint;
import com.example.assignment.global.exception.CustomException;
import com.example.assignment.global.exception.ExceptionType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtUtil.class, CustomAuthenticationEntryPoint.class, CustomAccessDeniedHandler.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    @Nested
    @DisplayName("사용자로 회원가입을 할 수 있다.")
    class SignUpUser {

        SignUpRequest userRequest = new SignUpRequest(
                "user@test.com",
                "password",
                "nickname"
        );

        @Test
        @DisplayName("회원가입에 성공한다.")
        void signUp() throws Exception {
            // given
            User testUser = new User(
                    1L,
                    userRequest.getEmail(),
                    userRequest.getPassword(),
                    userRequest.getNickname(),
                    UserRole.USER
            );
            given(authService.signUpUser(any())).willReturn(SignUpResponse.from(testUser));

            // when & then
            mockMvc.perform(
                    post("/signup/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(userRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").exists())
                    .andExpect(jsonPath("$.nickname").exists())
                    .andExpect(jsonPath("$.role").exists());
        }

        @Test
        @DisplayName("중복된 이메일로 회원가입 요청 시 예외가 발생한다.")
        void duplicated_email() throws Exception {
            // given
            given(authService.signUpUser(any())).willThrow(new CustomException(ExceptionType.USER_ALREADY_EXISTS));

            // when & then
            mockMvc.perform(
                            post("/signup/user")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(toJson(userRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error").exists())
                    .andExpect(jsonPath("$.error.code").value(ExceptionType.USER_ALREADY_EXISTS.name()))
                    .andExpect(jsonPath("$.error.message").value(ExceptionType.USER_ALREADY_EXISTS.getMessage()));
        }
    }

    @Nested
    @DisplayName("관리자로 회원가입을 할 수 있다.")
    class SignUpAdmin {

        SignUpRequest adminRequest = new SignUpRequest(
                "admin@test.com",
                "password",
                "nickname"
        );

        @Test
        @DisplayName("회원가입에 성공한다.")
        void signUp() throws Exception {
            // given
            User testUser = new User(
                    1L,
                    adminRequest.getEmail(),
                    adminRequest.getPassword(),
                    adminRequest.getNickname(),
                    UserRole.ADMIN
            );
            given(authService.signUpAdmin(any())).willReturn(SignUpResponse.from(testUser));

            // when & then
            mockMvc.perform(
                            post("/signup/admin")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(toJson(adminRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").exists())
                    .andExpect(jsonPath("$.nickname").exists())
                    .andExpect(jsonPath("$.role").exists());
        }

        @Test
        @DisplayName("중복된 이메일로 회원가입 요청 시 예외가 발생한다.")
        void duplicated_email() throws Exception {
            // given
            given(authService.signUpAdmin(any())).willThrow(new CustomException(ExceptionType.USER_ALREADY_EXISTS));

            // when & then
            mockMvc.perform(
                            post("/signup/admin")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(toJson(adminRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error").exists())
                    .andExpect(jsonPath("$.error.code").value(ExceptionType.USER_ALREADY_EXISTS.name()))
                    .andExpect(jsonPath("$.error.message").value(ExceptionType.USER_ALREADY_EXISTS.getMessage()));
        }
    }

    @Nested
    @DisplayName("로그인을 할 수 있다.")
    class LogIn {

        LoginRequest request = new LoginRequest("testUser@gmail.com", "password");

        @Test
        @DisplayName("로그인에 성공하여 token을 반환한다.")
        void login() throws Exception {
            // given
            String testToken = "test.token";
            LoginResponse response = new LoginResponse(testToken);

            given(authService.login(any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            post("/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(toJson(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").exists())
                    .andExpect(jsonPath("$.token").value(testToken));
        }

        @Test
        @DisplayName("존재하지 않는 email로 로그인 시도 시 예외가 발생한다.")
        void invalidCredentialsEmail() throws Exception {
            // given
            given(authService.login(any())).willThrow(new CustomException(ExceptionType.INVALID_CREDENTIALS));

            // when & then
            mockMvc.perform(
                            post("/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(toJson(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error").exists())
                    .andExpect(jsonPath("$.error.code").value(ExceptionType.INVALID_CREDENTIALS.name()))
                    .andExpect(jsonPath("$.error.message").value(ExceptionType.INVALID_CREDENTIALS.getMessage()));
        }

        @Test
        @DisplayName("일치하지 않는 password로 로그인 시도 시 예외가 발생한다.")
        void invalidCredentialsPassword() throws Exception {
            // given
            given(authService.login(any())).willThrow(new CustomException(ExceptionType.INVALID_CREDENTIALS));

            // when & then
            mockMvc.perform(
                            post("/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(toJson(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error").exists())
                    .andExpect(jsonPath("$.error.code").value(ExceptionType.INVALID_CREDENTIALS.name()))
                    .andExpect(jsonPath("$.error.message").value(ExceptionType.INVALID_CREDENTIALS.getMessage()));
        }
    }
}