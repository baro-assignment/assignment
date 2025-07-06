package com.example.assignment.global.auth;

import com.example.assignment.domain.user.entity.User;
import com.example.assignment.domain.user.enums.UserRole;
import com.example.assignment.domain.user.repository.UserRepository;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Spring Security 환경에서 인증(Authentication) 및 권한(Authorization) 제어가
 * 올바르게 동작하는지 통합적으로 검증하는 테스트 클래스.
 *
 * 주요 검증 내용 :
 * 1. 인증 없이 접근 (Anonymous)
 * 2. 사용자 권한으로 접근 (WithUserToken)
 * 3. 관리자 권한으로 접근 (WithAdminToken)
 *
 */

@SpringBootTest
@AutoConfigureMockMvc
@Import({SecurityConfig.class, JwtUtil.class, CustomAuthenticationEntryPoint.class, CustomAccessDeniedHandler.class})
public class AuthSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    private User user = new User(1L, "user@test.com", "password", "일반 사용자", UserRole.USER);
    private User adminUser = new User(2L, "admin@test.com", "password", "관리자", UserRole.ADMIN);
    private String userToken;
    private String adminToken;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        
        userRepository.save(user);
        userRepository.save(adminUser);

        userToken = jwtUtil.createBearerToken(user.getId(), user.getEmail(), user.getNickname(), user.getUserRole());
        adminToken = jwtUtil.createBearerToken(user.getId(), adminUser.getEmail(), adminUser.getNickname(), adminUser.getUserRole());
    }

    @Nested
    @DisplayName("인증 없이 접근")
    class Anonymous {

        @Test
        @DisplayName("인증 없이 인증이 필요한 API에 접근 시 401 에러를 전달한다.")
        void 인증없이_일반API에_접근시_401에러_리턴() throws Exception {
            mockMvc.perform(get("/users/me"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error").exists())
                    .andExpect(jsonPath("$.error.code").value(ExceptionType.AUTHENTICATION_REQUIRED.name()))
                    .andExpect(jsonPath("$.error.message").value(ExceptionType.AUTHENTICATION_REQUIRED.getMessage()));
        }

        @Test
        @DisplayName("인증 없이 관리자 권한이 필요한 API에 접근 시 401 에러를 전달한다.")
        void 인증없이_관리자API에_접근시_401에러_리턴() throws Exception {
            mockMvc.perform(get("/admin/users"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error").exists())
                    .andExpect(jsonPath("$.error.code").value(ExceptionType.AUTHENTICATION_REQUIRED.name()))
                    .andExpect(jsonPath("$.error.message").value(ExceptionType.AUTHENTICATION_REQUIRED.getMessage()));
        }

        @Test
        @DisplayName("인증 없이 회원가입, 로그인 API에 접근할 수 있다.")
        void signUpLogin() throws Exception {
            mockMvc.perform(post("/signup/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                                "email": "test@test.com",
                                "password": "password123",
                                "nickname": "test"
                            }
                        """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("test@test.com"));

            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                                "email": "test@test.com",
                                "password": "password123"
                            }
                        """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").exists());
        }
    }


    @Nested
    @DisplayName("사용자 권한으로 접근")
    class WithUserToken {

        @Test
        @DisplayName("사용자는 일반 API에 접근할 수 있다.")
        void api() throws Exception {
            mockMvc.perform(get("/users/me")
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("사용자가 관리자 API에 접근 시 403 에러를 전달한다.")
        void adminApi() throws Exception {
            mockMvc.perform(get("/admin/users")
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error").exists())
                    .andExpect(jsonPath("$.error.code").value(ExceptionType.ACCESS_DENIED.name()))
                    .andExpect(jsonPath("$.error.message").value(ExceptionType.ACCESS_DENIED.getMessage()));
        }
    }

    @Nested
    @DisplayName("관리자 권한으로 접근")
    class WithAdminToken {

        @Test
        @DisplayName("관리자는 일반 API에 접근할 수 있다.")
        void api() throws Exception {
            mockMvc.perform(get("/users/me")
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("관리자는 관리자 API에 접근할 수 있다.")
        void 관리자API에_접근시_200응답_리턴() throws Exception {
            mockMvc.perform(get("/admin/users")
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk());
        }
    }
}
