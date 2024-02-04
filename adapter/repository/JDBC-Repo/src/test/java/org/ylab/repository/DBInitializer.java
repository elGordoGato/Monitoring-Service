package org.ylab.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DBInitializer {
    public static void initDB(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA IF NOT EXISTS entities;" +
                    "CREATE SCHEMA IF NOT EXISTS service;");
            statement.execute("""
                    DROP TABLE IF EXISTS entities.users, entities.meters, entities.readings CASCADE;
                    CREATE TABLE entities.users (
                      id SERIAL PRIMARY KEY,
                      email VARCHAR(255) NOT NULL UNIQUE,
                      first_name VARCHAR(255) NOT NULL,
                      last_name VARCHAR(255) NOT NULL,
                      password VARCHAR(255) NOT NULL,
                      role VARCHAR(50) NOT NULL DEFAULT 'USER' CHECK (role IN ('USER', 'ADMIN'))
                    );
                    CREATE TABLE entities.meters (
                      id SMALLSERIAL PRIMARY KEY,
                      type VARCHAR(255) NOT NULL
                    );
                    CREATE TABLE entities.readings (
                      id BIGSERIAL PRIMARY KEY,
                      owner_id BIGINT NOT NULL REFERENCES entities.users(id),
                      meter_id SMALLINT NOT NULL REFERENCES entities.meters(id),
                      reading_value BIGINT NOT NULL,
                      collected_date TIMESTAMP NOT NULL
                    );
                    INSERT INTO entities.users (email, first_name, last_name, password, role)
                    VALUES ('admin@ylab.com', 'Gospodin', 'Intensiv', 'admin', 'ADMIN');
                    INSERT INTO entities.meters (type)
                    VALUES ('Cold water'), ('Hot water');
                    """);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
