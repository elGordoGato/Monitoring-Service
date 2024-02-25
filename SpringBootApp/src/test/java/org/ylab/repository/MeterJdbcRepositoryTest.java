package org.ylab.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.ylab.ConnectionManager;
import org.ylab.MigrationManager;
import org.ylab.adapter.repository.jdbcImpl.MeterJdbcRepository;
import org.ylab.entity.Meter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests for meter jdbc repository using test container")
@ActiveProfiles("tc")
@RequiredArgsConstructor
class MeterJdbcRepositoryTest {
    private static final PostgreSQLContainer<?> postgres = TestContainerManager.getContainer();

    private final MeterJdbcRepository meterJdbcRepository;

    @BeforeAll
    static void beforeAll() {
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
    @DisplayName("Test to find all meters saved to db")
@Transactional()
    public void testFindAll() {
        // given

        // when
        List<Meter> meters = meterJdbcRepository.findAll();

        // then
        assertThat(meters).hasSize(2);
        assertThat(meters.get(0).getType()).isEqualTo("Cold water");
        assertThat(meters.get(1).getType()).isEqualTo("Hot water");
    }

    @Test
    @DisplayName("Successfully find Meter type by it's ID")
    public void testGetById() {
        // given
        Meter meter = new Meter();
        meter.setType("Electricity");
        meter = meterJdbcRepository.save(meter);

        // when
        Optional<Meter> foundMeter = meterJdbcRepository.getById(meter.getId());

        // then
        assertThat(foundMeter).isPresent();
        assertThat(foundMeter.get().getType()).isEqualTo("Electricity");
    }

    @Test
    @DisplayName("Test save new meter to DB")
    public void testSave() {
        // given
        Meter meter = new Meter();
        meter.setType("Gas");

        // when
        Meter savedMeter = meterJdbcRepository.save(meter);

        // then
        assertThat(savedMeter.getId()).isNotNull();
        assertThat(savedMeter.getType()).isEqualTo("Gas");
    }
}