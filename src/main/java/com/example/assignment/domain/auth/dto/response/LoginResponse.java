package com.example.assignment.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "로그인 유지를 위한 액세스 토큰 전달")
public class LoginResponse {

    @Schema(description = "인증을 위한 AccessToken으로, " +
            "요청 시 `Authorization Header`에 포함되어 전달되어야 합니다.")
    private final String token;

    public LoginResponse(String token) {
        this.token = token;
    }
}
