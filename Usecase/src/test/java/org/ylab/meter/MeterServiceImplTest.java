package org.ylab.meter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

@DisplayName("Tests for meter type service functionality")
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
        meter.setId((short) 1);
        meter.setType("Electricity");
    }

    @Test
    @DisplayName("Test successfully return list of all meter types")
    void testGetAllSuccess() {
        List<Meter> expected = Collections.singletonList(meter);
        when(typeRepository.findAll()).thenReturn(expected);

        List<Meter> actual = meterService.getAll();

        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Test successfully return meter entity when requested by ID")
    void testGetByIdSuccess() {
        when(typeRepository.getById((short) 1)).thenReturn(Optional.of(meter));

        Meter actual = meterService.getById((short) 1);

        assertThat(actual)
                .isNotNull()
                .isEqualTo(meter);
    }

    @Test
    @DisplayName("Test throw not found exception when requested to find meter by ID and it was not found")
    void testGetByIdFailure() {
        when(typeRepository.getById((short) 1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> meterService.getById((short) 1));
    }
}