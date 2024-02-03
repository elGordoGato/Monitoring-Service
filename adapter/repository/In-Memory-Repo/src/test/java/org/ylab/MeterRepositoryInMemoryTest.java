package org.ylab;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ylab.entity.Meter;
import org.ylab.repository.MeterRepositoryInMemory;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests for in-memory meter type repository implementation")
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
    @DisplayName("Successfully find all meter types saved in memory")
    void testGetAllSuccess() {
        List<Meter> expected = List.of(coldWater, hotWater);

        List<Meter> actual = meterRepository.findAll();

        assertThat(actual)
                .isNotNull()
                .containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @DisplayName("Successfully find Meter type by it's ID")
    void testGetByIdSuccess() {
        Optional<Meter> expected = Optional.of(coldWater);

        Optional<Meter> actual = meterRepository.getById(1);

        assertThat(actual).isNotNull();
        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(expected.get());
    }

    @Test
    @DisplayName("Test return Optional.empty when no meter type with such ID stored")
    void testGetByIdFailure() {
        Optional<Meter> expected = Optional.empty();

        Optional<Meter> actual = meterRepository.getById(3);

        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }
}