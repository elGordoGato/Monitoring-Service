package org.ylab.repository;

import org.ylab.ConnectionManager;
import org.ylab.entity.Meter;
import org.ylab.entity.Reading;
import org.ylab.entity.User;
import org.ylab.port.ReadingRepository;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReadingJdbcRepository implements ReadingRepository {
    private final Connection connection;

    public ReadingJdbcRepository(ConnectionManager connectionManager) {
        connection = connectionManager.getConnection();
    }

    @Override
    public Reading save(Reading reading) {
        try (PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO entities.readings (owner_id, meter_id, reading_value, collected_date) " +
                        "VALUES (?,?,?,?) RETURNING id")) {
            Instant now = Instant.now();
            pstmt.setInt(1, reading.getOwner().getId());
            pstmt.setShort(2, reading.getMeter().getId());
            pstmt.setLong(3, reading.getReading());
            pstmt.setTimestamp(4, Timestamp.from(now));

            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            rs.next();
            reading.setId(rs.getLong("id"));
            reading.setCollectedDate(now);
            connection.commit();
            return reading;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Reading> findLastByUserAndType(User user, Meter type) {
        Reading foundReading = null;
        try (PreparedStatement pstmt = connection.prepareStatement(
                "SELECT id, collected_date FROM entities.readings WHERE owner_id = ? AND meter_id = ? " +
                        "ORDER BY collected_date DESC LIMIT 1")) {
            pstmt.setInt(1, user.getId());
            pstmt.setShort(2, type.getId());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                foundReading = new Reading();
                foundReading.setId(rs.getLong("id"));
                foundReading.setCollectedDate(rs.getTimestamp("collected_date").toInstant());
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(foundReading);
    }

    @Override
    public List<Reading> findActualByUser(User user) {
        List<Reading> foundActualReadings = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(
                "SELECT r1.*, m.type FROM entities.readings r1 JOIN entities.meters m on m.id = r1.meter_id " +
                        "WHERE r1.owner_id = ? AND r1.collected_date = (" +
                        "SELECT MAX(r2.collected_date) FROM entities.readings r2 WHERE r2.meter_id = r1.meter_id)")) {
            pstmt.setInt(1, user.getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                foundActualReadings.add(parseResultSet(rs));
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return foundActualReadings;
    }

    @Override
    public List<Reading> findActualByAdmin() {
        List<Reading> foundActualReadings = new ArrayList<>();
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT r1.*, m.type FROM entities.readings r1 " +
                    "JOIN entities.meters m on m.id = r1.meter_id WHERE r1.collected_date = (" +
                    "SELECT MAX(r2.collected_date) FROM entities.readings r2 WHERE r2.meter_id = r1.meter_id)");
            while (rs.next()) {
                foundActualReadings.add(parseResultSet(rs));
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return foundActualReadings;
    }

    @Override
    public List<Reading> findAllByOwnerAndDateBetween(User currentUser, Instant start, Instant end) {
        List<Reading> foundReadings = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(
                "SELECT r.*, m.type FROM entities.readings r JOIN entities.meters m on m.id = r.meter_id " +
                        "WHERE r.owner_id = ? AND r.collected_date BETWEEN ? AND ?")) {
            pstmt.setInt(1, currentUser.getId());
            pstmt.setTimestamp(2, Timestamp.from(start));
            pstmt.setTimestamp(3, Timestamp.from(end));

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                foundReadings.add(parseResultSet(rs));
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return foundReadings;
    }

    @Override
    public List<Reading> findAllByDateBetween(Instant start, Instant end) {
        List<Reading> foundReadings = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(
                "SELECT r.*, m.type FROM entities.readings r JOIN entities.meters m on m.id = r.meter_id " +
                        "WHERE r.collected_date BETWEEN ? AND ?")) {
            pstmt.setTimestamp(1, Timestamp.from(start));
            pstmt.setTimestamp(2, Timestamp.from(end));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                foundReadings.add(parseResultSet(rs));
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return foundReadings;
    }

    @Override
    public List<Reading> findAllByOwner(User currentUser) {
        List<Reading> foundReadings = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(
                "SELECT r.*, m.type FROM entities.readings r JOIN entities.meters m on m.id = r.meter_id " +
                        "WHERE r.owner_id = ?")) {
            pstmt.setInt(1, currentUser.getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                foundReadings.add(parseResultSet(rs));
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return foundReadings;
    }

    @Override
    public List<Reading> findAll() {
        List<Reading> readings = new ArrayList<>();
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT r.*, m.type FROM entities.readings r " +
                    "JOIN entities.meters m on m.id = r.meter_id");
            while (rs.next()) {
                readings.add(parseResultSet(rs));
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return readings;
    }

    private Reading parseResultSet(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        User owner = new User();
        owner.setId(rs.getInt("owner_id"));
        Meter meterType = new Meter();
        meterType.setId(rs.getShort("meter_id"));
        meterType.setType(rs.getString("type"));
        long readingValue = rs.getLong("reading_value");
        Instant collectedDate = rs.getTimestamp("collected_date").toInstant();

        return new Reading(id, owner, meterType, readingValue, collectedDate);
    }
}
