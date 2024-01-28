package org.ylab;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ylab.entity.User;
import org.ylab.enums.Role;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryInMemoryTest {
    private UserRepositoryInMemory userRepository;

    private User admin;
    private User user;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepositoryInMemory();

        admin = new User();
        admin.setId(UUID.randomUUID());
        admin.setEmail("admin@monitoring-service.org");
        admin.setPassword("admin");
        admin.setFirstName("Admin");
        admin.setLastName("Adminov");
        admin.setRole(Role.ADMIN);

        user = new User();
        user.setEmail("user@example.com");
        user.setPassword("user");
        user.setFirstName("User");
        user.setLastName("Userov");
        user.setRole(Role.USER);
    }

    @Test
    void testSaveSuccess() {
        User expected = user;

        User actual = userRepository.save(user);

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testGetByEmailSuccess() {
        userRepository.save(user);

        Optional<User> expected = Optional.of(user);

        Optional<User> actual = userRepository.findByEmail(user.getEmail());

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testGetByEmailFailure() {
        Optional<User> expected = Optional.empty();

        Optional<User> actual = userRepository.findByEmail("unknown@example.com");

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
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