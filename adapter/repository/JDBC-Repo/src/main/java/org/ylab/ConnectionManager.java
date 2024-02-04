package org.ylab;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private static final Logger log = LoggerFactory.getLogger(ConnectionManager.class);
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

            dbConnection.setAutoCommit(false);

        } catch (SQLException | ClassNotFoundException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        return dbConnection;
    }
}
