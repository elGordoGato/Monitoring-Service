package org.ylab;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionManager {
    private static final Logger log = LoggerFactory.getLogger(ConnectionManager.class);
    private static Connection dbConnection = null;

    public static Connection getConnection() {
        if (dbConnection != null) {
            return dbConnection;
        }
        try {
            String dbDriver = DatabaseConfig.getDriver();
            String connectionUrl = DatabaseConfig.getURL();
            String userName = DatabaseConfig.getUserName();
            String password = DatabaseConfig.getPassword();

            Class.forName(dbDriver);
            dbConnection = DriverManager.getConnection(connectionUrl,
                    userName, password);
            log.info("Connection with db is open");

            initSchema();

            MigrationManager.migrateDB(dbConnection);

            dbConnection.setAutoCommit(true);
        } catch (SQLException | ClassNotFoundException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        return dbConnection;
    }

    private static void initSchema() throws SQLException{
        Statement statement = dbConnection.createStatement();
        statement.execute("CREATE SCHEMA IF NOT EXISTS entities;" +
                "CREATE SCHEMA IF NOT EXISTS service;");
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

    public static void main(String[] args) {
        getConnection();
    }
}
