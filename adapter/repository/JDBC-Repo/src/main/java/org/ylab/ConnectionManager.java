package org.ylab;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class ConnectionManager {
    private static Connection dbConnection = null;
    private final String dbDriver;
    private final String connectionUrl;
    private final String userName;
    private final String password;

    public ConnectionManager(String dbDriver, String connectionUrl, String userName, String password) {
        this.dbDriver = dbDriver;
        this.connectionUrl = connectionUrl;
        this.userName = userName;
        this.password = password;
    }

    public static void closeConnection() {
        if (dbConnection != null) {
            try {
                dbConnection.close();
                log.info("Connection with db is closed");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Connection getConnection() {
        if (dbConnection != null) {
            return dbConnection;
        }
        try {
            Class.forName(dbDriver);
            dbConnection = DriverManager.getConnection(connectionUrl,
                    userName, password);
            log.info("Connection with db is open");

        } catch (SQLException | ClassNotFoundException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        return dbConnection;
    }
}
