package org.ylab.port;

import org.ylab.entity.User;

import java.util.Optional;

public interface UserRepository {
    /**
     * @param user User to be saved to db
     * @return User that has been saved
     */
    User save(User user);

    /**
     * @param email Email of user to be found
     * @return User with email
     */
    Optional<User> findByEmail(String email);
}
