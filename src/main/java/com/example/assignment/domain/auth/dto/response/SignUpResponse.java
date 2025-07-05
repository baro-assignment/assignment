package com.example.assignment.domain.auth.dto.response;

import com.example.assignment.domain.user.entity.User;
import com.example.assignment.domain.user.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "회원가입된 사용자의 정보")
public class SignUpResponse {

    @Schema(description = "회원가입 된 이메일")
    private final String email;
    @Schema(description = "닉네임")
    private final String nickname;
    @Schema(description = "역할")
    private final UserRole role;

    private SignUpResponse(String email, String nickname, UserRole role) {
        this.email = email;
        this.nickname = nickname;
        this.role = role;
    }

    public static SignUpResponse from(User user) {
        return new SignUpResponse(
                user.getEmail(),
                user.getNickname(),
                user.getUserRole()
        );
    }
}
