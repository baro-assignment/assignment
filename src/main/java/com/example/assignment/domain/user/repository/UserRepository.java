package com.example.assignment.domain.user.repository;

import com.example.assignment.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsername(String username);
    User save(User user);
    boolean existsByUsername(String username);
}
