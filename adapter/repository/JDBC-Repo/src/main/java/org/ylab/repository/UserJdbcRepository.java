package org.ylab.repository;

import org.ylab.entity.User;
import org.ylab.enums.Role;
import org.ylab.port.UserRepository;

import java.util.Optional;
import java.util.UUID;

public class UserJdbcRepository implements UserRepository {

    /**
     * @param user User to be saved to db
     * @return
     */
    @Override
    public User save(User user) {
        return null;
    }

    /**
     * @param email Email of user to be found
     * @return
     */
    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }
}
