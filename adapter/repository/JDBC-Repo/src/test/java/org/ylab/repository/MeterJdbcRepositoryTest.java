package org.ylab.repository;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.ylab.ConnectionManager;
import org.ylab.MigrationManager;
import org.ylab.entity.Meter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests for meter jdbc repository using test container")
class MeterJdbcRepositoryTest {
    private static final PostgreSQLContainer<?> postgres = TestContainerManager.getContainer();

    private static MeterJdbcRepository meterJdbcRepository;
    private static Connection connection;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
        ConnectionManager connectionProvider = new ConnectionManager(
                postgres.getDriverClassName(),
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );
        connection = connectionProvider.getConnection();
        meterJdbcRepository = new MeterJdbcRepository(connection);
        MigrationManager.migrateDB(connection, "main");
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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