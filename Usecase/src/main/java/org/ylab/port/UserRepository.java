package org.ylab.port;

import org.ylab.entity.UserEntity;

import java.util.Optional;

public interface UserRepository {
    /**
     * @param user User to be saved to db
     * @return User that has been saved
     */
    UserEntity save(UserEntity user);

    /**
     * @param email Email of user to be found
     * @return User with email
     */
    Optional<UserEntity> findByEmail(String email);
}
