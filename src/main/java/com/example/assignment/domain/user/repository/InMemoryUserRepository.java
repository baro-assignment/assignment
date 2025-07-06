package com.example.assignment.domain.user.repository;

import com.example.assignment.domain.user.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(users.get(email));
    }

    @Override
    public Optional<User> findById(Long userId) {
        return users.values().stream().filter(user -> user.getId().equals(userId)).findFirst();
    }

    @Override
    public User save(User user) {
        Long id = (user.getId() == null) ? idGenerator.getAndIncrement() : user.getId();
        User savedUser = User.builder()
                .id(id)
                .email(user.getEmail())
                .password(user.getPassword())
                .nickname(user.getNickname())
                .userRole(user.getUserRole())
                .build();
        users.put(user.getEmail(), savedUser);
        return savedUser;
    }

    @Override
    public boolean existsByEmail(String email) {
        return users.containsKey(email);
    }

    @Override
    public List<User> getAll() {
        return users.values().stream().toList();
    }

    @Override
    public void deleteAll() {
        users.clear();
    }
}
