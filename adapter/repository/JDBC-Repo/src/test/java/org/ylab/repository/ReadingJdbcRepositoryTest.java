package org.ylab.repository;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.ylab.ConnectionManager;
import org.ylab.MigrationManager;
import org.ylab.entity.Meter;
import org.ylab.entity.Reading;
import org.ylab.entity.User;
import org.ylab.enums.Role;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests for jdbc meter readings repository functionality using test container")
class ReadingJdbcRepositoryTest {
    private static final PostgreSQLContainer<?> postgres = TestContainerManager.getContainer();
    private static ReadingJdbcRepository readingRepository;
    private static Connection connection;

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
        readingRepository = new ReadingJdbcRepository(connectionProvider);
        MigrationManager.migrateDB(connection, "main, test");
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

    @AfterEach
    void rollback() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Successfully save new meter reading")
    public void testSave() {
        // given
        User user = new User(
                13, "test@test.com", "Test", "User", "secret", Role.USER);
        Meter meter = new Meter((short) 1, "Cold water");
        Reading reading = new Reading(null, user, meter, 100, Instant.now());

        // when
        Reading savedReading = readingRepository.save(reading);

        // then
        assertThat(savedReading.getId()).isNotNull();
        assertThat(savedReading.getOwner()).isEqualTo(user);
        assertThat(savedReading.getMeter()).isEqualTo(meter);
        assertThat(savedReading.getReading()).isEqualTo(100);
    }

    @Test
    @DisplayName("Successfully find last submitted meter reading of selected meter type by user")
    public void testFindLastByUserAndType() {
        // given
        User user = new User();
        user.setId(13);
        Meter meter = new Meter();
        meter.setId((short) 1);


        // when
        Optional<Reading> foundReading = readingRepository.findLastByUserAndType(user, meter);

        // then
        assertThat(foundReading).isPresent();
        String stringDate = "2024-03-15 09:58:21.872000";
        String pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDateTime = LocalDateTime.parse(stringDate, dateTimeFormatter);
        Instant instant = localDateTime.atZone(ZoneOffset.UTC).toInstant();
        assertThat(foundReading.get().getCollectedDate()).isEqualTo(instant);
    }

    @Test
    @DisplayName("Test to successfully find actual readings submitted bu user")
    public void testFindActualByUser() {
        // given
        User user = new User(
                13, "test@test.com", "Test", "User", "secret", Role.USER);
        Meter meter1 = new Meter((short) 1, "Cold water");
        Meter meter2 = new Meter((short) 2, "Hot water");
        Reading reading1 = new Reading(null, user, meter1, 50, Instant.now());
        Reading reading2 = new Reading(null, user, meter2, 40, Instant.now());
        Reading reading3 = new Reading(null, user, meter1, 30, Instant.now().plusSeconds(10));
        readingRepository.save(reading1);
        readingRepository.save(reading2);
        readingRepository.save(reading3);

        // when
        List<Reading> actualReadings = readingRepository.findActualByUser(user);

        // then
        assertThat(actualReadings).hasSize(2);
        assertThat(actualReadings.get(0).getReading()).isEqualTo(114);
        assertThat(actualReadings.get(1).getReading()).isEqualTo(40);
    }

    @Test
    @DisplayName("Successfully find all readings submitted by user within selected period")
    public void testFindAllByOwnerAndDateBetween() {
        // given
        User user = new User(
                13, "test@test.com", "Test", "User", "secret", Role.USER);

        // when
        List<Reading> readings = readingRepository.findAllByOwnerAndDateBetween(
                user, Instant.parse("2024-01-01T00:00:00Z"), Instant.parse("2024-01-28T23:59:59Z"));

        // then
        assertThat(readings).hasSize(1);
    }

    @Test
    @DisplayName("Test to successfully find by admin actual readings for each meter type submitted by any user before")
    public void testFindActualByAdmin() {
        // given
        User user1 = new User(
                13, "user1@test.com", "User", "One", "secret", Role.USER);
        User user2 = new User(
                14, "user2@test.com", "User", "Two", "secret", Role.USER);
        Meter meter1 = new Meter((short) 1, "Cold water");
        Meter meter2 = new Meter((short) 2, "Hot water");
        Reading reading1 = new Reading(null, user1, meter1, 100, Instant.now());
        Reading reading2 = new Reading(null, user2, meter2, 200, Instant.now());
        Reading reading3 = new Reading(null, user1, meter1, 150, Instant.now().plusSeconds(10));
        Reading reading4 = new Reading(null, user2, meter2, 250, Instant.now().plusSeconds(10));
        readingRepository.save(reading1);
        readingRepository.save(reading2);
        readingRepository.save(reading3);
        readingRepository.save(reading4);

        // when
        List<Reading> actualReadings = readingRepository.findActualByAdmin();

        // then
        assertThat(actualReadings).hasSize(2);
    }

    @Test
    @DisplayName("Test successfully return list of all readings submitted by anybody within selected period")
    public void testFindAllByDateBetween() {
        // when
        List<Reading> readings = readingRepository.findAllByDateBetween(
                Instant.parse("2024-02-01T00:00:00Z"), Instant.parse("2024-02-28T23:59:59Z"));

        // then
        assertThat(readings).hasSize(2);
    }

    @Test
    @DisplayName("Test successfully return list of all readings submitted by selected user")
    public void testFindAllByOwner() {
        // given
        User user1 = new User(
                13, "user1@test.com", "User", "One", "secret", Role.USER);

        // when
        List<Reading> readings = readingRepository.findAllByOwner(user1);

        // then
        assertThat(readings).hasSize(2);
    }

    @Test
    @DisplayName("Test return list of all readings submitted")
    public void testFindAll() {
        // when
        List<Reading> readings = readingRepository.findAll();

        // then
        assertThat(readings).hasSize(4);
    }
}