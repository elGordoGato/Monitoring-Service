package org.ylab;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class MigrationManager {
    private static final String CREATE_SCHEMA_QUERY = """
            CREATE SCHEMA IF NOT EXISTS entities;
            CREATE SCHEMA IF NOT EXISTS service;""";
    private static final String changelog = "db/changelog/changelog.xml";

    public static void migrateDB(Connection connection) {
        try {
            initSchema(connection);

            Database database =
                    DatabaseFactory.getInstance().findCorrectDatabaseImplementation(
                            new JdbcConnection(connection));
            //TODO Вопрос к знатокам: После выполнения последней строки autoCommitMode у connection переводится в false, почему?

            database.setDefaultSchemaName("entities");
            database.setLiquibaseSchemaName("service");

            Liquibase liquibase =
                    new Liquibase(changelog,
                            new ClassLoaderResourceAccessor(),
                            database);
            liquibase.update(new Contexts());
            log.info("Migration is completed successfully");
            connection.setAutoCommit(true);
        } catch (LiquibaseException e) {
            log.error("SQL Exception in migration {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void initSchema(Connection dbConnection) {

        try (Statement statement = dbConnection.createStatement()) {
            statement.executeUpdate(CREATE_SCHEMA_QUERY);
            log.info("Schema initialized");
        } catch (SQLException e) {
            log.error("SQL exception during initializing schema");
            throw new RuntimeException(e);
        }
    }
}
