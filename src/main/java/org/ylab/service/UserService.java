package org.ylab.service;

import org.ylab.dto.UserDto;
import org.ylab.entity.User;
import org.ylab.repository.UserRepository;

public interface UserService {
    User create(User user);

    User authenticate(String email, String password);
}
