package org.ylab;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ylab.entity.Meter;
import org.ylab.entity.Reading;
import org.ylab.entity.User;

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
    void testFindActualByUserSuccess() {
        readingRepository.save(reading);

        List<Reading> expected = List.of(reading);

        List<Reading> actual = readingRepository.findActualByUser(user);

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testFindActualByUserEmpty() {
        List<Reading> expected = List.of();

        List<Reading> actual = readingRepository.findActualByUser(user);

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testFindActualByAdminSuccess() {
        readingRepository.save(reading);

        List<Reading> expected = List.of(reading);

        List<Reading> actual = readingRepository.findActualByAdmin();

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testFindActualByAdminEmpty() {
        List<Reading> expected = List.of();

        List<Reading> actual = readingRepository.findActualByAdmin();

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testGetLastByUserAndTypeSuccess() {
        readingRepository.save(reading);

        Optional<Reading> expected = Optional.of(reading);

        Optional<Reading> actual = readingRepository.findLastByUserAndType(user, meter);

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testGetLastByUserAndTypeEmpty() {
        Optional<Reading> expected = Optional.empty();

        Optional<Reading> actual = readingRepository.findLastByUserAndType(user, meter);

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testSaveSuccess() {
        Reading expected = reading;

        Reading actual = readingRepository.save(reading);

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testFindAllByOwnerAndDateBetweenSuccess() {
        readingRepository.save(reading);

        LocalDate date = LocalDate.now();
        Instant start = date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        Instant end = date.plusMonths(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();

        List<Reading> expected = List.of(reading);

        List<Reading> actual = readingRepository.findAllByOwnerAndDateBetween(user, start, end);

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testFindAllByOwnerAndDateBetweenEmpty() {
        LocalDate date = LocalDate.now();
        Instant start = date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        Instant end = date.plusMonths(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();

        List<Reading> expected = List.of();

        List<Reading> actual = readingRepository.findAllByOwnerAndDateBetween(user, start, end);

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testFindAllByDateBetweenSuccess() {
        readingRepository.save(reading);

        LocalDate date = LocalDate.now();
        Instant start = date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        Instant end = date.plusMonths(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();

        List<Reading> expected = List.of(reading);

        List<Reading> actual = readingRepository.findAllByDateBetween(start, end);

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testFindAllByDateBetweenEmpty() {
        LocalDate date = LocalDate.now();
        Instant start = date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        Instant end = date.plusMonths(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();

        List<Reading> expected = List.of();

        List<Reading> actual = readingRepository.findAllByDateBetween(start, end);

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testFindAllByOwnerSuccess() {
        readingRepository.save(reading);

        List<Reading> expected = List.of(reading);

        List<Reading> actual = readingRepository.findAllByOwner(user);

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testFindAllByOwnerEmpty() {
        List<Reading> expected = List.of();

        List<Reading> actual = readingRepository.findAllByOwner(user);

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testFindAllSuccess() {
        readingRepository.save(reading);

        List<Reading> expected = List.of(reading);

        List<Reading> actual = readingRepository.findAll();

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testFindAllEmpty() {
        List<Reading> expected = List.of();

        List<Reading> actual = readingRepository.findAll();

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }
}