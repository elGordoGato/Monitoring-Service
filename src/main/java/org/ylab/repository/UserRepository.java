package org.ylab.repository;

import org.ylab.entity.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Optional<User> getByEmail(String email);
}
