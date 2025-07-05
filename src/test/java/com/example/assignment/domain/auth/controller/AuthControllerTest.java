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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

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

    private SignUpRequest uniqueSingUpRequest() {
        String randomString = UUID.randomUUID().toString();
        return new SignUpRequest(
                "user@"+ randomString,
                "password",
                "nickname_"+ randomString,
                UserRole.USER
        );
    }

    @Nested
    class 회원가입 {
        @Test
        void 성공시_200응답_리턴() throws Exception {
            // given
            SignUpRequest request = uniqueSingUpRequest();
            User testUser = new User(
                    1L,
                    request.getEmail(),
                    request.getPassword(),
                    request.getNickname(),
                    uniqueSingUpRequest().getRole()
            );

            given(authService.singUp(any())).willReturn(SignUpResponse.from(testUser));

            // when & then
            mockMvc.perform(
                    post("/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").exists())
                    .andExpect(jsonPath("$.nickname").exists())
                    .andExpect(jsonPath("$.role").exists());
        }

        @Nested
        class 실패 {
            @Test
            void 중복된_email이라면_409에러_리턴() throws Exception {
                // given
                SignUpRequest request = uniqueSingUpRequest();

                given(authService.singUp(any())).willThrow(new CustomException(ExceptionType.USER_ALREADY_EXISTS));

                // when & then
                mockMvc.perform(
                                post("/signup")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(toJson(request)))
                        .andExpect(status().isConflict())
                        .andExpect(jsonPath("$.error").exists())
                        .andExpect(jsonPath("$.error.code").value(ExceptionType.USER_ALREADY_EXISTS.name()))
                        .andExpect(jsonPath("$.error.message").value(ExceptionType.USER_ALREADY_EXISTS.getMessage()));
            }
        }
    }

    @Nested
    class 로그인 {
        @Test
        void 성공시_200응답과_토큰_리턴() throws Exception {
            // given
            String testToken = "test.token";
            LoginRequest request = new LoginRequest("testUser@gmail.com", "password");
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

        @Nested
        class 실패 {
            @Test
            void 존재하지않는_email이라면_401에러_리턴() throws Exception {
                // given
                LoginRequest request = new LoginRequest("testUser@gmail.com", "password");

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
            void 일치하지않는_password이라면_401에러_리턴() throws Exception {
                // given
                LoginRequest request = new LoginRequest("testUser@gmail.com", "wrongPassword");

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
}