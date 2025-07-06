package com.example.assignment.domain.auth;

import com.example.assignment.domain.auth.dto.request.LoginRequest;
import com.example.assignment.domain.auth.dto.request.SignUpRequest;
import com.example.assignment.global.exception.ExceptionType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
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

    private SignUpRequest uniqueSignUpRequest() {
        String randomString = UUID.randomUUID().toString();
        return new SignUpRequest(
                "testUser"+ randomString + "@test.com",
                "password",
                "nickname_"+ randomString
        );
    }

    @Nested
    @DisplayName("사용자 회원가입")
    class SignUpUser {

        @Test
        @DisplayName("회원가입에 성공하여 200 응답을 리턴한다.")
        void signUpUser() throws Exception {
            SignUpRequest signUpRequest = uniqueSignUpRequest();

            mockMvc.perform(
                            post("/signup/user")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(toJson(signUpRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value(signUpRequest.getEmail()));
        }

        @Test
        @DisplayName("중복된 이메일로 요청 시 409 에러로 응답한다.")
        void duplicatedEmail() throws Exception {
            SignUpRequest signUpRequest = uniqueSignUpRequest();

            // 첫 가입
            mockMvc.perform(
                            post("/signup/user")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(toJson(signUpRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").exists());

            // 동일한 email으로 중복 가입 시도
            mockMvc.perform(
                            post("/signup/user")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(toJson(signUpRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error").exists())
                    .andExpect(jsonPath("$.error.code").value(ExceptionType.USER_ALREADY_EXISTS.name()))
                    .andExpect(jsonPath("$.error.message").value(ExceptionType.USER_ALREADY_EXISTS.getMessage()));
        }

        @Test
        @DisplayName("필수입력값을 누락하여 요청 시 400 에러로 응답한다.")
        void requestValidationFail() throws Exception {
            // nickname 누락 요청
            SignUpRequest signUpRequest = new SignUpRequest(
                    "testUser@test.com",
                    "password1",
                    null
            );

            mockMvc.perform(
                            post("/signup/user")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(toJson(signUpRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists())
                    .andExpect(jsonPath("$.error.code").value(ExceptionType.REQUEST_VALIDATION_FAILED.name()))
                    .andExpect(jsonPath("$.error.message").exists());
        }
    }

    @Nested
    @DisplayName("관리자 회원가입")
    class SignUpAdmin {

        @Test
        @DisplayName("회원가입에 성공하여 200 응답을 리턴한다.")
        void signUpUser() throws Exception {
            SignUpRequest signUpRequest = uniqueSignUpRequest();

            mockMvc.perform(
                            post("/signup/user")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(toJson(signUpRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value(signUpRequest.getEmail()));
        }

        @Test
        @DisplayName("중복된 이메일로 요청 시 409 에러로 응답한다.")
        void duplicatedEmail() throws Exception {
            SignUpRequest signUpRequest = uniqueSignUpRequest();

            // 첫 가입
            mockMvc.perform(
                            post("/signup/user")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(toJson(signUpRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").exists());

            // 동일한 email으로 중복 가입 시도
            mockMvc.perform(
                            post("/signup/admin")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(toJson(signUpRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error").exists())
                    .andExpect(jsonPath("$.error.code").value(ExceptionType.USER_ALREADY_EXISTS.name()))
                    .andExpect(jsonPath("$.error.message").value(ExceptionType.USER_ALREADY_EXISTS.getMessage()));
        }

        @Test
        @DisplayName("필수입력값을 누락하여 요청 시 400 에러로 응답한다.")
        void requestValidationFail() throws Exception {
            // nickname 누락 요청
            SignUpRequest signUpRequest = new SignUpRequest(
                    "testUser@test.com",
                    "password1",
                    null
            );

            mockMvc.perform(
                            post("/signup/admin")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(toJson(signUpRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists())
                    .andExpect(jsonPath("$.error.code").value(ExceptionType.REQUEST_VALIDATION_FAILED.name()))
                    .andExpect(jsonPath("$.error.message").exists());
        }
    }

    @Nested
    @DisplayName("로그인")
    class Login {

        @Test
        @DisplayName("사용자 로그인 성공 시 200 응답과 함께 토큰을 전달한다.")
        void loginUser() throws Exception {
            // 사용자로 회원가입
            SignUpRequest signUpRequest = uniqueSignUpRequest();
            mockMvc.perform(
                            post("/signup/user")
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

        @Test
        @DisplayName("관리자 로그인 성공 시 200 응답과 함께 토큰을 전달한다.")
        void loginAdmin() throws Exception {
            // 관리자로 회원가입
            SignUpRequest signUpRequest = uniqueSignUpRequest();
            mockMvc.perform(
                            post("/signup/admin")
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

        @Test
        @DisplayName("존재하지 않는 이메일로 로그인 요청 시 401 에러로 응답한다.")
        void invalidCredentialsEmail() throws Exception {
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
        @DisplayName("일치하지 않는 비밀번호로 로그인 요청 시 401 에러로 응답한다.")
        void invalidCredentialsPassword() throws Exception {
            // 먼저 사용자 회원가입
            SignUpRequest signUpRequest = uniqueSignUpRequest();
            mockMvc.perform(
                            post("/signup/user")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(toJson(signUpRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").exists());

            // 잘못된 password로 로그인 요청
            LoginRequest loginRequest = new LoginRequest(uniqueSignUpRequest().getEmail(), "wrongPassword");
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
        @DisplayName("필수입력값을 누락하여 요청 시 400 에러로 응답한다.")
        void 필수입력값_누락이라면_400에러_리턴() throws Exception {
            // password 누락 요청
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