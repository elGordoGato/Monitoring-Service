package org.ylab;

import org.ylab.entity.User;
import org.ylab.enums.Role;
import org.ylab.port.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class UserRepositoryInMemory implements UserRepository {
    Map<String, User> userMap = new HashMap<>();

    public UserRepositoryInMemory() {
        User admin = new User();
        admin.setId(UUID.randomUUID());
        admin.setEmail("admin@monitoring-service.org");
        admin.setPassword("admin");
        admin.setFirstName("Admin");
        admin.setLastName("Adminov");
        admin.setRole(Role.ADMIN);
        userMap.put(admin.getEmail(), admin);
    }

    @Override
    public User save(User user) {
        user.setId(UUID.randomUUID());
        userMap.put(user.getEmail(), user);
        return user;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userMap.get(email));
    }
}
