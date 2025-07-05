package com.example.assignment.domain.auth;

import com.example.assignment.domain.auth.dto.request.LoginRequest;
import com.example.assignment.domain.auth.dto.request.SignUpRequest;
import com.example.assignment.domain.user.enums.UserRole;
import com.example.assignment.global.exception.ExceptionType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    private SignUpRequest uniqueSingUpRequest() {
        String randomString = UUID.randomUUID().toString();
        return new SignUpRequest(
                "testUser"+ randomString + "@test.com",
                "password",
                "nickname_"+ randomString,
                UserRole.USER
        );
    }

    @Nested
    class 회원가입 {
        @Test
        void 성공시_200응답_리턴() throws Exception {
            SignUpRequest signUpRequest = uniqueSingUpRequest();

            mockMvc.perform(
                            post("/signup")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(toJson(signUpRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value(signUpRequest.getEmail()));
        }

        @Nested
        class 실패 {
            @Test
            void 중복된_email이라면_409에러_리턴() throws Exception {
                SignUpRequest signUpRequest = uniqueSingUpRequest();

                // 첫 가입
                mockMvc.perform(
                                post("/signup")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(toJson(signUpRequest)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.email").exists());

                // 동일한 email으로 중복 가입 시도
                mockMvc.perform(
                                post("/signup")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(toJson(signUpRequest)))
                        .andExpect(status().isConflict())
                        .andExpect(jsonPath("$.error").exists())
                        .andExpect(jsonPath("$.error.code").value(ExceptionType.USER_ALREADY_EXISTS.name()))
                        .andExpect(jsonPath("$.error.message").value(ExceptionType.USER_ALREADY_EXISTS.getMessage()));
            }

            @Test
            void 필수입력값_누락이라면_400에러_리턴() throws Exception {
                SignUpRequest signUpRequest = new SignUpRequest(
                        "testUser@test.com",
                        "password1",
                        null,
                        UserRole.USER
                );

                mockMvc.perform(
                                post("/signup")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(toJson(signUpRequest)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.error").exists())
                        .andExpect(jsonPath("$.error.code").value(ExceptionType.REQUEST_VALIDATION_FAILED.name()))
                        .andExpect(jsonPath("$.error.message").exists());
            }
        }
    }

    @Nested
    class 로그인 {
        @Test
        void 성공시_200응답과_토큰_리턴() throws Exception {
            // 먼저 회원가입
            SignUpRequest signUpRequest = uniqueSingUpRequest();
            mockMvc.perform(
                            post("/signup")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(toJson(signUpRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").exists());

            // 로그인 요청
            LoginRequest loginRequest = new LoginRequest(signUpRequest.getEmail(), signUpRequest.getPassword());
            mockMvc.perform(
                            post("/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(toJson(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").exists());
        }

        @Nested
        class 실패 {
            @Test
            void 존재하지않는_email이라면_401에러_리턴() throws Exception {
                // 로그인 요청
                LoginRequest loginRequest = new LoginRequest("testUser@test.com", "password1");
                mockMvc.perform(
                                post("/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(toJson(loginRequest)))
                        .andExpect(status().isUnauthorized())
                        .andExpect(jsonPath("$.error").exists())
                        .andExpect(jsonPath("$.error.code").value(ExceptionType.INVALID_CREDENTIALS.name()))
                        .andExpect(jsonPath("$.error.message").value(ExceptionType.INVALID_CREDENTIALS.getMessage()));
            }

            @Test
            void 일치하지않는_password이라면_401에러_리턴() throws Exception {
                // 먼저 회원가입
                SignUpRequest signUpRequest = uniqueSingUpRequest();
                mockMvc.perform(
                                post("/signup")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(toJson(signUpRequest)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.email").exists());

                // 잘못된 password로 로그인 요청
                LoginRequest loginRequest = new LoginRequest(uniqueSingUpRequest().getEmail(), "wrongPassword");
                mockMvc.perform(
                                post("/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(toJson(loginRequest)))
                        .andExpect(status().isUnauthorized())
                        .andExpect(jsonPath("$.error").exists())
                        .andExpect(jsonPath("$.error.code").value(ExceptionType.INVALID_CREDENTIALS.name()))
                        .andExpect(jsonPath("$.error.message").value(ExceptionType.INVALID_CREDENTIALS.getMessage()));
            }

            @Test
            void 필수입력값_누락이라면_400에러_리턴() throws Exception {
                LoginRequest loginRequest = new LoginRequest("testUser@test.com", null);
                mockMvc.perform(
                                post("/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(toJson(loginRequest)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.error").exists())
                        .andExpect(jsonPath("$.error.code").value(ExceptionType.REQUEST_VALIDATION_FAILED.name()))
                        .andExpect(jsonPath("$.error.message").exists());
            }
        }
    }
}