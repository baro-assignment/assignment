package com.example.assignment.domain.auth.dto.response;

import com.example.assignment.domain.user.entity.User;
import com.example.assignment.domain.user.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "회원가입된 사용자의 정보")
public class SignUpResponse {

    @Schema(description = "사용자를 구분하는 이름")
    private final String username;
    @Schema(description = "닉네임")
    private final String nickname;
    @Schema(description = "역할")
    private final UserRole role;

    private SignUpResponse(String username, String nickname, UserRole role) {
        this.username = username;
        this.nickname = nickname;
        this.role = role;
    }

    public static SignUpResponse from(User user) {
        return new SignUpResponse(
                user.getUsername(),
                user.getNickname(),
                user.getUserRole()
        );
    }
}
