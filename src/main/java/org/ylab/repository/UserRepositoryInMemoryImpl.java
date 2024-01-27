package org.ylab.repository;

import org.ylab.entity.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class UserRepositoryInMemoryImpl implements UserRepository {
    Map<String, User> userMap = new HashMap<>();
    @Override
    public User save(User user) {
        user.setId(UUID.randomUUID());
        userMap.put(user.getEmail(), user);
        return user;
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return Optional.ofNullable(userMap.get(email));
    }
}
