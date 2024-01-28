package org.ylab.meter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ylab.entity.Meter;
import org.ylab.exception.NotFoundException;
import org.ylab.port.MeterRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeterServiceImplTest {

    @Mock
    private MeterRepository typeRepository;

    @InjectMocks
    private MeterServiceImpl meterService;

    private Meter meter;

    @BeforeEach
    void setUp() {
        meter = new Meter();
        meter.setId(1);
        meter.setType("Electricity");
    }

    @Test
    void testGetAllSuccess() {
        List<Meter> expected = Collections.singletonList(meter);
        when(typeRepository.getAll()).thenReturn(expected);

        List<Meter> actual = meterService.getAll();

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testGetByIdSuccess() {
        when(typeRepository.getById(1)).thenReturn(Optional.of(meter));

        Meter actual = meterService.getById(1);

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(meter);
    }

    @Test
    void testGetByIdFailure() {
        when(typeRepository.getById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> meterService.getById(1));
    }
}