package org.ylab.repository;


import org.ylab.ConnectionManager;
import org.ylab.entity.Meter;
import org.ylab.port.MeterRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MeterJdbcRepository implements MeterRepository {
    private final Connection connection;

    public MeterJdbcRepository() {
        connection = ConnectionManager.getConnection();
    }

    @Override
    public List<Meter> findAll() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM entities.meters;");
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
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM entities.meters WHERE id = ?;");
            statement.setShort(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Meter meter = new Meter(
                        resultSet.getShort("id"),
                        resultSet.getString("type"));
                return Optional.of(meter);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Meter save(Meter meter) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "INSERT INTO entities.meters (type) VALUES (?) RETURNING id");
            pstmt.setString(1, meter.getType());
            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            rs.next();
            meter.setId(rs.getShort("id"));
            return meter;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
