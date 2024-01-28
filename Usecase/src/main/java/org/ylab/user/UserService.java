package org.ylab.user;

import org.ylab.entity.User;

public interface UserService {
    User create(User user);

    User authenticate(String email, String password);
}
