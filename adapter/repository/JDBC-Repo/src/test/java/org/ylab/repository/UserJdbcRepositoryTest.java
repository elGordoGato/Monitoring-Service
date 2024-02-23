package org.ylab.repository;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.ylab.ConnectionManager;
import org.ylab.MigrationManager;
import org.ylab.entity.UserEntity;
import org.ylab.enums.Role;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests for jdbc repository implementation for work with user entity using test container")
public class UserJdbcRepositoryTest {

    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine");

    private static UserJdbcRepository userJdbcRepository;
    private static Connection connection;
    private UserEntity user;

    @BeforeAll
    static void setUp() {
        postgres.start();
        ConnectionManager connectionProvider = new ConnectionManager(
                postgres.getDriverClassName(),
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );
        connection = connectionProvider.getConnection();
        userJdbcRepository = new UserJdbcRepository(connection);
        MigrationManager.migrateDB(connection, "main");
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void setUser() {
        user = new UserEntity();
        user.setEmail("test@test.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("secret");
    }

    @AfterEach
    void rollback() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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