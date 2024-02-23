package org.ylab.reading;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ylab.entity.Meter;
import org.ylab.entity.Reading;
import org.ylab.entity.UserEntity;
import org.ylab.port.ReadingRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("Tests of reading admin service functionality")
@ExtendWith(MockitoExtension.class)
class ReadingAdminServiceImplTest {

    @Mock
    private ReadingRepository readingRepository;

    @InjectMocks
    private ReadingAdminServiceImpl readingService;

    private UserEntity user;
    private Reading reading;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setEmail("test@example.com");
        user.setPassword("secret");

        Meter meter = new Meter();
        meter.setType("Electricity");

        reading = new Reading();
        reading.setMeter(meter);
        reading.setOwner(user);
        reading.setReading(100);
        reading.setCollectedDate(Instant.now());
    }

    @Test
    @DisplayName("Test to successfully get all actual readings submitted to db for each type by any user")
    void testGetActualSuccess() {
        List<Reading> expected = Collections.singletonList(reading);
        when(readingRepository.findActualByAdmin()).thenReturn(expected);

        List<Reading> actual = readingService.getActual(user);

        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Test to successfully return all readings submitted within selected month by any user")
    void testGetForMonthSuccess() {
        LocalDate date = LocalDate.now();
        Instant start = date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        Instant end = date.plusMonths(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        List<Reading> expected = Collections.singletonList(reading);
        when(readingRepository.findAllByDateBetween(start, end)).thenReturn(expected);

        List<Reading> actual = readingService.getForMonth(user, date);

        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Test to successfully return all readings submitted to db")
    void testGetAllByUserSuccess() {
        List<Reading> expected = Collections.singletonList(reading);
        when(readingRepository.findAll()).thenReturn(expected);

        List<Reading> actual = readingService.getAllByUser(user);

        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }
}