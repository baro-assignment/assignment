package com.example.assignment.domain.auth.dto.request;

import com.example.assignment.domain.user.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "회원가입을 위한 사용자 정보 전달")
@Getter
@AllArgsConstructor
public class SignUpRequest {

    @Email
    @NotBlank
    @Schema(description = "회원가입을 진행할 이메일, 중복된 이메일로 회원가입할 수 없습니다.", example = "test@gmail.com")
    private String email;

    @NotBlank
    @Schema(description = "사용자 인증을 위한 비밀번호", example = "password")
    private String password;

    @NotBlank
    @Schema(description = "닉네임", example = "닉네임")
    private String nickname;
}
