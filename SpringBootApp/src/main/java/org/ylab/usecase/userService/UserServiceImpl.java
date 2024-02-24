package org.ylab.usecase.userService;

import org.springframework.stereotype.Service;
import org.ylab.domain.entity.UserEntity;
import org.ylab.domain.exception.BadRequestException;
import org.ylab.domain.exception.ConflictException;
import org.ylab.domain.exception.NotFoundException;
import org.ylab.usecase.port.UserRepository;

@Service
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
