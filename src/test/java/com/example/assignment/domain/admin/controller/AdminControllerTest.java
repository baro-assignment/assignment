package com.example.assignment.domain.admin.controller;

import com.example.assignment.domain.admin.dto.response.GrantAdminRoleResponse;
import com.example.assignment.domain.admin.service.AdminService;
import com.example.assignment.domain.user.entity.User;
import com.example.assignment.domain.user.enums.UserRole;
import com.example.assignment.global.auth.jwt.JwtAuthenticationToken;
import com.example.assignment.global.auth.jwt.JwtUtil;
import com.example.assignment.global.auth.security.config.SecurityConfig;
import com.example.assignment.global.auth.security.handler.CustomAccessDeniedHandler;
import com.example.assignment.global.auth.security.handler.CustomAuthenticationEntryPoint;
import com.example.assignment.global.dto.AuthInfo;
import com.example.assignment.global.exception.CustomException;
import com.example.assignment.global.exception.ExceptionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@Import({SecurityConfig.class, JwtUtil.class, CustomAuthenticationEntryPoint.class, CustomAccessDeniedHandler.class})
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    private JwtAuthenticationToken userAuthenticationToken;
    private JwtAuthenticationToken adminAuthenticationToken;

    @BeforeEach
    public void setUp() {
        AuthInfo userAuthInfo = new AuthInfo("user", "일반 사용자", UserRole.USER);
        userAuthenticationToken = new JwtAuthenticationToken(userAuthInfo);
        AuthInfo adminAuthInfo = new AuthInfo("admin", "관리자", UserRole.ADMIN);
        adminAuthenticationToken = new JwtAuthenticationToken(adminAuthInfo);
    }

    @Nested
    class 관리자_권한부여 {
        @Test
        void 성공시_200응답_리턴() throws Exception {
            // given
            Long targetUserId = 1L;
            User targetUser = User.builder()
                    .username("target")
                    .nickname("nickname")
                    .userRole(UserRole.USER)
                    .password("password").build();
            GrantAdminRoleResponse response = GrantAdminRoleResponse.from(targetUser);
            given(adminService.grantAdminRoleToUser(anyLong())).willReturn(response);

            // when & then
            mockMvc.perform(patch("/admin/users/{userId}/grant", targetUserId)
                    .with(authentication(adminAuthenticationToken))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").exists())
                    .andExpect(jsonPath("$.nickname").exists())
                    .andExpect(jsonPath("$.role").exists());
        }

        @Nested
        class 실패 {
            @Test
            void 일반사용자의_요청이라면_403에러_리턴() throws Exception {
                // given
                Long targetUserId = 1L;
                User targetUser = User.builder()
                        .username("target")
                        .nickname("nickname")
                        .userRole(UserRole.USER)
                        .password("password").build();
                GrantAdminRoleResponse response = GrantAdminRoleResponse.from(targetUser);
                given(adminService.grantAdminRoleToUser(anyLong())).willReturn(response);

                // when & then
                mockMvc.perform(patch("/admin/users/{userId}/grant", targetUserId)
                                .with(authentication(userAuthenticationToken))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isForbidden())
                        .andExpect(jsonPath("$.error").exists())
                        .andExpect(jsonPath("$.error.code").value(ExceptionType.ACCESS_DENIED.name()))
                        .andExpect(jsonPath("$.error.message").value(ExceptionType.ACCESS_DENIED.getMessage()));
            }

            @Test
            void 존재하지_않는_사용자라면_404에러_리턴() throws Exception {
                // given
                Long doesntExistUserId = 1L;
                given(adminService.grantAdminRoleToUser(doesntExistUserId)).willThrow(
                        new CustomException(ExceptionType.USER_NOT_FOUND)
                );

                // when & then
                mockMvc.perform(patch("/admin/users/{userId}/grant", doesntExistUserId)
                                .with(authentication(adminAuthenticationToken))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.error").exists())
                        .andExpect(jsonPath("$.error.code").value(ExceptionType.USER_NOT_FOUND.name()))
                        .andExpect(jsonPath("$.error.message").value(ExceptionType.USER_NOT_FOUND.getMessage()));
            }

        }
    }
}