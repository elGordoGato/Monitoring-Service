package org.ylab;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ylab.entity.Meter;
import org.ylab.entity.Reading;
import org.ylab.entity.User;
import org.ylab.repository.ReadingRepositoryInMemory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests for in memory meter readings repository functionality")
class ReadingRepositoryInMemoryTest {

    private ReadingRepositoryInMemory readingRepository;

    private User user;
    private Meter meter;
    private Reading reading;

    @BeforeEach
    void setUp() {
        readingRepository = new ReadingRepositoryInMemory();

        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("secret");

        meter = new Meter();
        meter.setType("Electricity");

        reading = new Reading();
        reading.setMeter(meter);
        reading.setOwner(user);
        reading.setReading(100);
        reading.setCollectedDate(Instant.now());
    }

    @Test
    @DisplayName("Test to successfully find actual readings submitted bu user")
    void testFindActualByUserSuccess() {
        readingRepository.save(reading);

        List<Reading> expected = List.of(reading);

        List<Reading> actual = readingRepository.findActualByUser(user);

        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Test return empty List of actual readings when no readings were submitted by user before")
    void testFindActualByUserEmpty() {
        List<Reading> expected = List.of();

        List<Reading> actual = readingRepository.findActualByUser(user);

        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Test to successfully find by admin actual readings for each meter type submitted by any user before")
    void testFindActualByAdminSuccess() {
        readingRepository.save(reading);

        List<Reading> expected = List.of(reading);

        List<Reading> actual = readingRepository.findActualByAdmin();

        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Test to return empty list when requested by admin to find actual readings and " +
            "no readings were submitted before by any user")
    void testFindActualByAdminEmpty() {
        List<Reading> expected = List.of();

        List<Reading> actual = readingRepository.findActualByAdmin();

        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Successfully find last submitted meter reading of selected meter type by user")
    void testGetLastByUserAndTypeSuccess() {
        readingRepository.save(reading);

        Optional<Reading> expected = Optional.of(reading);

        Optional<Reading> actual = readingRepository.findLastByUserAndType(user, meter);

        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Test find last submitted meter reading of selected meter type by user " +
            "return Optional.empty when no readings were submitted by user with selected type")
    void testGetLastByUserAndTypeEmpty() {
        Optional<Reading> expected = Optional.empty();

        Optional<Reading> actual = readingRepository.findLastByUserAndType(user, meter);

        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Successfully save new meter reading")
    void testSaveSuccess() {
        Reading expected = reading;

        Reading actual = readingRepository.save(reading);

        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Successfully find all readings submitted by user within selected period")
    void testFindAllByOwnerAndDateBetweenSuccess() {
        readingRepository.save(reading);

        LocalDate date = LocalDate.now();
        Instant start = date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        Instant end = date.plusMonths(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();

        List<Reading> expected = List.of(reading);

        List<Reading> actual = readingRepository.findAllByOwnerAndDateBetween(user, start, end);

        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Return optional.empty when no readings were submitted by user within selected period")
    void testFindAllByOwnerAndDateBetweenEmpty() {
        LocalDate date = LocalDate.now();
        Instant start = date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        Instant end = date.plusMonths(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();

        List<Reading> expected = List.of();

        List<Reading> actual = readingRepository.findAllByOwnerAndDateBetween(user, start, end);

        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Test successfully return list of all readings submitted by anybody within selected period")
    void testFindAllByDateBetweenSuccess() {
        readingRepository.save(reading);

        LocalDate date = LocalDate.now();
        Instant start = date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        Instant end = date.plusMonths(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();

        List<Reading> expected = List.of(reading);

        List<Reading> actual = readingRepository.findAllByDateBetween(start, end);

        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Test return empty list when no readings were submitted by anybody within selected period")
    void testFindAllByDateBetweenEmpty() {
        LocalDate date = LocalDate.now();
        Instant start = date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        Instant end = date.plusMonths(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();

        List<Reading> expected = List.of();

        List<Reading> actual = readingRepository.findAllByDateBetween(start, end);

        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Test successfully return list of all readings submitted by selected user")
    void testFindAllByOwnerSuccess() {
        readingRepository.save(reading);

        List<Reading> expected = List.of(reading);

        List<Reading> actual = readingRepository.findAllByOwner(user);

        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Test return empty list if no readings were submitted before by selected user")
    void testFindAllByOwnerEmpty() {
        List<Reading> expected = List.of();

        List<Reading> actual = readingRepository.findAllByOwner(user);

        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Test return list of all readings submitted")
    void testFindAllSuccess() {
        readingRepository.save(reading);

        List<Reading> expected = List.of(reading);

        List<Reading> actual = readingRepository.findAll();

        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Test return empty list if no readings were submitted")
    void testFindAllEmpty() {
        List<Reading> expected = List.of();

        List<Reading> actual = readingRepository.findAll();

        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }
}