package org.ylab.jdbcRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.ylab.domain.entity.Meter;
import org.ylab.jdbcImpl.MeterJdbcRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests for meter jdbc repository using test container")
@SpringBootTest
@ActiveProfiles("tc")
@Import(ContainersConfig.class)
class MeterJdbcRepositoryTest {
    @Autowired
    private Connection connection;
    @Autowired
    private MeterJdbcRepository meterJdbcRepository;

    @BeforeEach
    void setUp() throws SQLException {
        connection.setAutoCommit(false);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.rollback();
    }

    @Test
    @DisplayName("Test to find all meters saved to db")
    @Rollback
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