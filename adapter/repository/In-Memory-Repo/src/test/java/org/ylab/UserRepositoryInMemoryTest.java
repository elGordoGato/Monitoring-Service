package org.ylab;

import jdk.jfr.Frequency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ylab.entity.User;
import org.ylab.enums.Role;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests for in memory repository implementation for work with user entity")
class UserRepositoryInMemoryTest {
    private UserRepositoryInMemory userRepository;

    private User admin;
    private User user;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepositoryInMemory();

        admin = new User();
        admin.setId(UUID.randomUUID());
        admin.setEmail("admin");
        admin.setPassword("admin");
        admin.setFirstName("Gospodin");
        admin.setLastName("Intensiv");
        admin.setRole(Role.ADMIN);

        user = new User();
        user.setEmail("user@example.com");
        user.setPassword("user");
        user.setFirstName("Bob");
        user.setLastName("Examplov");
        user.setRole(Role.USER);
    }

    @Test
    @DisplayName("Successfully save new user to db")
    void testSaveSuccess() {
        User expected = user;

        User actual = userRepository.save(user);

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Test successfully find user by email field")
    void testGetByEmailSuccess() {
        userRepository.save(user);

        Optional<User> expected = Optional.of(user);

        Optional<User> actual = userRepository.findByEmail(user.getEmail());

        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Test return optional.empty when no user with such email in db")
    void testGetByEmailFailure() {
        Optional<User> expected = Optional.empty();

        Optional<User> actual = userRepository.findByEmail("unknown@example.com");

        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Test to successfully return admin by email")
    void testGetAdminByEmailSuccess() {
        Optional<User> expected = Optional.of(admin);

        Optional<User> actual = userRepository.findByEmail(admin.getEmail());

        assertThat(actual).isNotNull();
        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get().getEmail()).isEqualTo(expected.get().getEmail());
        assertThat(actual.get().getFirstName()).isEqualTo(expected.get().getFirstName());
        assertThat(actual.get().getRole()).isEqualTo(Role.ADMIN);
    }
}