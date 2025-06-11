package com.example.assignment.domain.user.repository;

import com.example.assignment.domain.user.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(users.get(username));
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
                .username(user.getUsername())
                .password(user.getPassword())
                .nickname(user.getNickname())
                .userRole(user.getUserRole())
                .build();
        users.put(user.getUsername(), savedUser);
        return savedUser;
    }

    @Override
    public boolean existsByUsername(String username) {
        return users.values().stream().anyMatch(user -> user.getUsername().equals(username));
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
