package org.ylab.jdbcRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.ylab.domain.entity.UserEntity;
import org.ylab.domain.enums.Role;
import org.ylab.jdbcImpl.UserJdbcRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests for jdbc repository implementation for work with user entity using test container")
@SpringBootTest
@ActiveProfiles("tc")
@Import(ContainersConfig.class)
public class UserJdbcRepositoryTest {
    @Autowired
    private Connection connection;
    @Autowired
    private UserJdbcRepository userJdbcRepository;
    private UserEntity user;

    @BeforeEach
    void setUp() throws SQLException {
        connection.setAutoCommit(false);
        user = new UserEntity();
        user.setEmail("test@test.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("secret");
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.rollback();
    }

    @Test
    @DisplayName("Successfully save new user to db")
    public void testSaveUser() {
        // when
        UserEntity savedUser = userJdbcRepository.save(user);

        // then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("Test successfully find user by email field")
    public void testFindByEmail() {
        // given
        userJdbcRepository.save(user);

        // when
        Optional<UserEntity> foundUser = userJdbcRepository.findByEmail("test@test.com");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getFirstName()).isEqualTo("Test");
        assertThat(foundUser.get().getLastName()).isEqualTo("User");
    }
}