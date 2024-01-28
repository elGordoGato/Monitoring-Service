package org.ylab.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ylab.entity.User;
import org.ylab.exception.*;
import org.ylab.port.UserRepository;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("secret");
    }

    @Test
    void testCreateUserSuccess() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        User createdUser = userService.create(user);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(createdUser.getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    void testCreateUserFailure() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.ofNullable(user));

        assertThrows(ConflictException.class, () -> userService.create(user));
    }

    @Test
    void testAuthenticateUserSuccess() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.ofNullable(user));

        User authenticatedUser = userService.authenticate(user.getEmail(), user.getPassword());

        assertThat(authenticatedUser).isNotNull();
        assertThat(authenticatedUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(authenticatedUser.getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    void testAuthenticateUserNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.authenticate(user.getEmail(), user.getPassword()));
    }

    @Test
    void testAuthenticateUserWrongPassword() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.ofNullable(user));

        assertThrows(BadRequestException.class, () -> userService.authenticate(user.getEmail(), "wrong"));
    }
}