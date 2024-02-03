package org.ylab.repository;


import org.ylab.ConnectionManager;
import org.ylab.entity.Meter;
import org.ylab.port.MeterRepository;

import java.sql.*;
import java.util.*;

public class MeterJdbcRepository implements MeterRepository {
    private final Connection connection;

    public MeterJdbcRepository() {
        connection = ConnectionManager.getConnection();
    }


    /**
     * @return
     */
    @Override
    public List<Meter> findAll() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM entities.meters;");
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


    /**
     * @param id ID of meter type
     * @return
     */
    @Override
    public Optional<Meter> getById(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("");
            ResultSet resultSet = statement.executeQuery("SELECT * FROM entities.meters;");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    /**
     * @param meter new type of meter to be saved to db
     * @return
     */
    @Override
    public Meter save(Meter meter) {
        return null;
    }
}
