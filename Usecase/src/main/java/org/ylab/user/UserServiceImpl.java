package org.ylab.user;

import org.ylab.entity.UserEntity;
import org.ylab.exception.BadRequestException;
import org.ylab.exception.ConflictException;
import org.ylab.exception.NotFoundException;
import org.ylab.port.UserRepository;

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserEntity create(UserEntity user) {
        userRepository.findByEmail(user.getEmail()).ifPresent(u -> {
            throw new ConflictException(String.format("User with email: %s already exists", u.getEmail()));
        });

        return userRepository.save(user);
    }

    @Override
    public UserEntity authenticate(String email, String password) {
        UserEntity loginUser = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new NotFoundException(String.format("User with email: %s not found", email)));
        if (!loginUser.getPassword().equals(password)) {
            throw new BadRequestException("Wrong password");
        }
        return loginUser;
    }


}
