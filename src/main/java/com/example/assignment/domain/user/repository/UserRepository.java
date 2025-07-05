package com.example.assignment.domain.user.repository;

import com.example.assignment.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long userId);
    User save(User user);
    boolean existsByEmail(String email);
    List<User> getAll();
    void deleteAll();
}
