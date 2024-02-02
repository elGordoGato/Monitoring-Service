package org.ylab.user;

import org.ylab.entity.User;

public interface UserService {
    /**
     * @param user User to be registered
     * @return User that has been registered
     */
    User create(User user);

    /**
     * @param email    email of user to authenticate
     * @param password password of user to authenticate
     * @return User that has been authenticated
     */
    User authenticate(String email, String password);
}
