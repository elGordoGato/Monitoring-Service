package org.ylab.reading;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ylab.entity.Meter;
import org.ylab.entity.Reading;
import org.ylab.entity.User;
import org.ylab.port.ReadingRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReadingAdminServiceImplTest {

    @Mock
    private ReadingRepository readingRepository;

    @InjectMocks
    private ReadingAdminServiceImpl readingService;

    private User user;
    private Reading reading;

    @BeforeEach
    void setUp() {
        user = new User();
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
    void testGetActualSuccess() {
        List<Reading> expected = Collections.singletonList(reading);
        when(readingRepository.findActualByAdmin()).thenReturn(expected);

        List<Reading> actual = readingService.getActual(user);

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testGetForMonthSuccess() {
        LocalDate date = LocalDate.now();
        Instant start = date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        Instant end = date.plusMonths(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        List<Reading> expected = Collections.singletonList(reading);
        when(readingRepository.findAllByDateBetween(start, end)).thenReturn(expected);

        List<Reading> actual = readingService.getForMonth(user, date);

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testGetAllByUserSuccess() {
        List<Reading> expected = Collections.singletonList(reading);
        when(readingRepository.findAll()).thenReturn(expected);

        List<Reading> actual = readingService.getAllByUser(user);

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }
}