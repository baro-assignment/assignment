package com.example.assignment.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "로그인을 위해 필요한 정보 전달")
@Getter
@AllArgsConstructor
public class LoginRequest {

    @NotBlank
    @Schema(description = "로그인할 이메일", example = "user1@gmail.com")
    private String email;

    @NotBlank
    @Schema(description = "사용자 인증을 위한 비밀번호", example = "password")
    private String password;
}
