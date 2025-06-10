package com.example.assignment.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Schema(description = "회원가입을 위한 사용자 정보 전달")
@Getter
public class SignUpRequest {

    @NotBlank
    @Schema(description = "사용자를 구분하기 위한 이름", example = "SUBIN0620")
    private String username;

    @NotBlank
    @Schema(description = "사용자 인증을 위한 비밀번호", example = "password123")
    private String password;

    @NotBlank
    @Schema(description = "닉네임", example = "수빈")
    private String nickname;
}
