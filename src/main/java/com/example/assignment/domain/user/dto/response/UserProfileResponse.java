package com.example.assignment.domain.user.dto.response;

import com.example.assignment.domain.user.entity.User;
import com.example.assignment.domain.user.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "사용자 프로필 정보")
public class UserProfileResponse {

    @Schema(description = "사용자를 구분하는 아이디")
    private final Long id;
    @Schema(description = "사용자를 구분하는 이름")
    private final String username;
    @Schema(description = "닉네임")
    private final String nickname;
    @Schema(description = "역할")
    private final UserRole role;

    private UserProfileResponse(Long id, String username, String nickname, UserRole role) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.role = role;
    }

    public static UserProfileResponse of(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getUserRole()
        );
    }
}
