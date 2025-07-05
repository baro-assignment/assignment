package com.example.assignment.domain.admin.dto.response;

import com.example.assignment.domain.user.entity.User;
import com.example.assignment.domain.user.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "관리자 역할이 부여된 사용자의 정보")
public class GrantAdminRoleResponse {

    @Schema(description = "사용자 id")
    private Long id;
    @Schema(description = "사용자 이메일")
    private final String email;
    @Schema(description = "닉네임")
    private final String nickname;
    @Schema(description = "역할")
    private final UserRole role;

    private GrantAdminRoleResponse(Long id, String email, String nickname, UserRole role) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
    }

    public static GrantAdminRoleResponse from(User user) {
        return new GrantAdminRoleResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getUserRole()
        );
    }
}
