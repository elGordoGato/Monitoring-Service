package org.ylab;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class MigrationManager {
    private static final String changelog = "db/changelog/changelog.xml";

    public static void migrateDB(Connection connection) {
        try {
            initSchema(connection);

            Database database =
                    DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            database.setDefaultSchemaName("entities");
            database.setLiquibaseSchemaName("service");

            Liquibase liquibase =
                    new Liquibase(changelog,
                            new ClassLoaderResourceAccessor(),
                            database);
            liquibase.update(new Contexts());
            log.info("Migration is completed successfully");
        } catch (LiquibaseException e) {
            log.error("SQL Exception in migration {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private static void initSchema(Connection dbConnection) {

        try (Statement statement = dbConnection.createStatement()) {
            statement.execute("CREATE SCHEMA IF NOT EXISTS entities;" +
                    "CREATE SCHEMA IF NOT EXISTS service;");
            log.info("Schema initialized");
        } catch (SQLException e) {
            log.error("SQL exception during initializing schema");
            throw new RuntimeException(e);
        }
    }
}
