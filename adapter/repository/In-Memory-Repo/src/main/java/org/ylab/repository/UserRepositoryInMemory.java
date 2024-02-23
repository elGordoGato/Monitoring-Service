package org.ylab.repository;

import org.ylab.entity.UserEntity;
import org.ylab.enums.Role;
import org.ylab.port.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserRepositoryInMemory implements UserRepository {
    private final Map<String, UserEntity> userMap = new HashMap<>();
    private int id = 1;

    public UserRepositoryInMemory() {
        UserEntity admin = new UserEntity();
        admin.setId(id++);
        admin.setEmail("admin");
        admin.setPassword("admin");
        admin.setFirstName("Gospodin");
        admin.setLastName("Intensiv");
        admin.setRole(Role.ADMIN);
        userMap.put(admin.getEmail(), admin);
    }

    @Override
    public UserEntity save(UserEntity user) {
        user.setId(id++);
        userMap.put(user.getEmail(), user);
        return user;
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return Optional.ofNullable(userMap.get(email));
    }
}
