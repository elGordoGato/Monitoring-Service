package org.ylab;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ylab.entity.Meter;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class MeterRepositoryInMemoryTest {

    private MeterRepositoryInMemory meterRepository;

    private Meter coldWater;
    private Meter hotWater;

    @BeforeEach
    void setUp() {
        meterRepository = new MeterRepositoryInMemory();

        coldWater = new Meter();
        coldWater.setId(1);
        coldWater.setType("Cold water");

        hotWater = new Meter();
        hotWater.setId(2);
        hotWater.setType("Hot water");
    }

    @Test
    void testGetAllSuccess() {
        List<Meter> expected = List.of(coldWater, hotWater);

        List<Meter> actual = meterRepository.getAll();

        assertThat(actual).isNotNull();
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void testGetByIdSuccess() {
        Optional<Meter> expected = Optional.of(coldWater);

        Optional<Meter> actual = meterRepository.getById(1);

        assertThat(actual).isNotNull();
        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(expected.get());
    }

    @Test
    void testGetByIdFailure() {
        Optional<Meter> expected = Optional.empty();

        Optional<Meter> actual = meterRepository.getById(3);

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }
}