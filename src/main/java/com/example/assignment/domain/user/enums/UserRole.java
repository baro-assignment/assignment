package com.example.assignment.domain.user.enums;

public enum UserRole {
    USER, ADMIN;

    public String toRoleName() {
        return "ROLE_" + this.name(); // Spring Security "ROLE_" prefix 관례
    }
}
