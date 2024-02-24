package org.ylab.usecase.userService;

import org.ylab.domain.entity.UserEntity;

public interface UserService {
    /**
     * @param user User to be registered
     * @return User that has been registered
     */
    UserEntity create(UserEntity user);

    /**
     * @param email    email of user to authenticate
     * @param password password of user to authenticate
     * @return User that has been authenticated
     */
    UserEntity authenticate(String email, String password);
}
