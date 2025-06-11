package com.example.assignment.domain.user.repository;

import com.example.assignment.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long userId);
    User save(User user);
    boolean existsByUsername(String username);
    List<User> getAll();
    void deleteAll();
}
