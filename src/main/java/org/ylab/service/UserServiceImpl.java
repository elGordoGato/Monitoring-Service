package org.ylab.service;

import org.ylab.entity.User;
import org.ylab.exception.BadRequestException;
import org.ylab.exception.ConflictException;
import org.ylab.exception.NotFoundException;
import org.ylab.repository.UserRepository;
import org.ylab.repository.UserRepositoryInMemoryImpl;

public class UserServiceImpl implements UserService {
    UserRepository userRepository = new UserRepositoryInMemoryImpl();

    @Override
    public User create(User user) {
        userRepository.getByEmail(user.getEmail()).ifPresent(u -> {
            throw new ConflictException(String.format("User with email: %s already exists", u.getEmail()));});

        return userRepository.save(user);
    }

    /**
     * @param email
     * @param password
     * @return
     */
    @Override
    public User authenticate(String email, String password) {
        User loginUser = userRepository.getByEmail(email).orElseThrow(() -> new NotFoundException(String.format("User with email: %s not found", email)));
        if (!loginUser.getPassword().equals(password)){
            throw new BadRequestException("Wrong password");
        }
        return loginUser;
    }


}
