package org.ylab.adapter.repository.jdbcImpl;

import org.springframework.stereotype.Repository;
import org.ylab.domain.entity.Meter;
import org.ylab.domain.entity.Reading;
import org.ylab.domain.entity.UserEntity;
import org.ylab.usecase.port.ReadingRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.time.Instant.now;

@Repository
public class ReadingJdbcRepository implements ReadingRepository {
    private final static String SAVE_QUERY = """
            INSERT INTO entities.readings (owner_id, meter_id, reading_value, collected_date)
            VALUES (?,?,?,?)""";
    private final static String FIND_LAST_BY_USER_AND_TYPE_QUERY = """
            SELECT id, collected_date FROM entities.readings WHERE owner_id = ? AND meter_id = ?
            ORDER BY collected_date DESC LIMIT 1;""";
    private final static String FIND_ACTUAL_BY_USER_QUERY = """
            SELECT r1.*, m.type FROM entities.readings r1 JOIN entities.meters m on m.id = r1.meter_id
            WHERE r1.owner_id = ? AND r1.collected_date = (SELECT MAX(r2.collected_date)
                                                           FROM entities.readings r2
                                                           WHERE r2.meter_id = r1.meter_id)""";
    private final static String FIND_ACTUAL_BY_ADMIN_QUERY = """
            SELECT r1.*, m.type FROM entities.readings r1
            JOIN entities.meters m on m.id = r1.meter_id
            WHERE r1.collected_date = (SELECT MAX(r2.collected_date)
                                       FROM entities.readings r2
                                       WHERE r2.meter_id = r1.meter_id)""";
    private final static String FIND_ALL_BY_OWNER_AND_DATE_BETWEEN_QUERY = """
            SELECT r.*, m.type FROM entities.readings r JOIN entities.meters m ON m.id = r.meter_id
            WHERE r.owner_id = ? AND r.collected_date BETWEEN ? AND ?""";
    private final static String FIND_ALL_BY_DATE_BETWEEN_QUERY = """
            SELECT r.*, m.type FROM entities.readings r JOIN entities.meters m ON m.id = r.meter_id
            WHERE r.collected_date BETWEEN ? AND ?""";
    private final static String FIND_ALL_BY_OWNER_QUERY = """
            SELECT r.*, m.type FROM entities.readings r JOIN entities.meters m ON m.id = r.meter_id
            WHERE r.owner_id = ?""";
    private final static String FIND_ALL_QUERY = """
            SELECT r.*, m.type FROM entities.readings r
            JOIN entities.meters m on m.id = r.meter_id""";
    private final Connection connection;

    public ReadingJdbcRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Reading save(Reading reading) {
        try (PreparedStatement pstmt = connection.prepareStatement(
                SAVE_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            Instant now = now();
            pstmt.setInt(1, reading.getOwner().getId());
            pstmt.setShort(2, reading.getMeter().getId());
            pstmt.setLong(3, reading.getReading());
            pstmt.setTimestamp(4, Timestamp.from(now));

            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            rs.next();
            reading.setId(rs.getLong(1));
            reading.setCollectedDate(now);
            rs.close();
            return reading;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Reading> findLastByUserAndType(UserEntity user, Meter type) {
        Reading foundReading = null;
        try (PreparedStatement pstmt = connection.prepareStatement(FIND_LAST_BY_USER_AND_TYPE_QUERY)) {
            pstmt.setInt(1, user.getId());
            pstmt.setShort(2, type.getId());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                foundReading = new Reading();
                foundReading.setId(rs.getLong("id"));
                foundReading.setCollectedDate(rs.getTimestamp("collected_date").toInstant());
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(foundReading);
    }

    @Override
    public List<Reading> findActualByUser(UserEntity user) {
        return getReadings(user, FIND_ACTUAL_BY_USER_QUERY);
    }

    @Override
    public List<Reading> findActualByAdmin() {
        return getReadings(FIND_ACTUAL_BY_ADMIN_QUERY);
    }

    @Override
    public List<Reading> findAllByOwnerAndDateBetween(UserEntity currentUser, Instant start, Instant end) {
        List<Reading> foundReadings = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(
                FIND_ALL_BY_OWNER_AND_DATE_BETWEEN_QUERY)) {
            pstmt.setInt(1, currentUser.getId());
            pstmt.setTimestamp(2, Timestamp.from(start));
            pstmt.setTimestamp(3, Timestamp.from(end));

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Reading foundReading = parseResultSet(rs);
                foundReadings.add(foundReading);
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return foundReadings;
    }

    @Override
    public List<Reading> findAllByDateBetween(Instant start, Instant end) {
        List<Reading> foundReadings = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(
                FIND_ALL_BY_DATE_BETWEEN_QUERY)) {
            pstmt.setTimestamp(1, Timestamp.from(start));
            pstmt.setTimestamp(2, Timestamp.from(end));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Reading foundReading = parseResultSet(rs);
                foundReadings.add(foundReading);
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return foundReadings;
    }

    @Override
    public List<Reading> findAllByOwner(UserEntity currentUser) {
        return getReadings(currentUser, FIND_ALL_BY_OWNER_QUERY);
    }


    @Override
    public List<Reading> findAll() {
        return getReadings(FIND_ALL_QUERY);
    }

    private List<Reading> getReadings(UserEntity currentUser, String findAllByOwnerQuery) {
        List<Reading> foundReadings = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(
                findAllByOwnerQuery)) {
            pstmt.setInt(1, currentUser.getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Reading foundReading = parseResultSet(rs);
                foundReadings.add(foundReading);
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return foundReadings;
    }


    private List<Reading> getReadings(String findAllQuery) {
        List<Reading> readings = new ArrayList<>();
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(findAllQuery);
            while (rs.next()) {
                readings.add(parseResultSet(rs));
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return readings;
    }

    private Reading parseResultSet(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        UserEntity owner = new UserEntity();
        owner.setId(rs.getInt("owner_id"));
        Meter meterType = new Meter();
        meterType.setId(rs.getShort("meter_id"));
        meterType.setType(rs.getString("type"));
        long readingValue = rs.getLong("reading_value");
        Instant collectedDate = rs.getTimestamp("collected_date").toInstant();

        return new Reading(id, owner, meterType, readingValue, collectedDate);
    }
}
