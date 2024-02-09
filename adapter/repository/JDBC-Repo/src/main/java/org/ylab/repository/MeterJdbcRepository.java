package org.ylab.repository;


import org.ylab.ConnectionManager;
import org.ylab.entity.Meter;
import org.ylab.port.MeterRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MeterJdbcRepository implements MeterRepository {
    private static final String FIND_ALL_QUERY = "SELECT * FROM entities.meters;";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM entities.meters WHERE id = ?;";
    private static final String SAVE_QUERY = "INSERT INTO entities.meters (type) VALUES (?)";
    private final Connection connection;

    public MeterJdbcRepository(ConnectionManager connectionManager) {
        connection = connectionManager.getConnection();
    }

    @Override
    public List<Meter> findAll() {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(
                    FIND_ALL_QUERY);
            List<Meter> meters = new ArrayList<>();
            while (resultSet.next()) {
                short id = resultSet.getShort("id");
                String type = resultSet.getString("type");
                meters.add(new Meter(id, type));
            }
            return meters;
        } catch (SQLException e) {
            System.out.println("Got SQL Exception " + e.getMessage());
        }
        return null;
    }

    @Override
    public Optional<Meter> getById(short id) {
        Meter meter = null;
        try (PreparedStatement statement = connection.prepareStatement(
                FIND_BY_ID_QUERY)) {
            statement.setShort(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                meter = new Meter(
                        resultSet.getShort("id"),
                        resultSet.getString("type"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(meter);
    }

    @Override
    public Meter save(Meter meter) {
        try (PreparedStatement pstmt = connection.prepareStatement(
                SAVE_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, meter.getType());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            rs.next();
            meter.setId(rs.getShort(1));
            return meter;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
