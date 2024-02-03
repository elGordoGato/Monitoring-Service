package org.ylab;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class MigrationManager {
    private static final Logger log = LoggerFactory.getLogger(MigrationManager.class);

    public static void migrateDB(Connection connection) {
        try {
            Database database =
                    DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            database.setDefaultSchemaName("entities");
            database.setLiquibaseSchemaName("service");
            Liquibase liquibase =
                    new Liquibase("db/changelog/changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update(new Contexts());
            log.info("Migration is completed successfully");
        } catch (LiquibaseException e) {
            log.error("SQL Exception in migration {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
