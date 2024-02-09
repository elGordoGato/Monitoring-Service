package org.ylab.repository;

import org.testcontainers.containers.PostgreSQLContainer;

public class TestContainerManager {
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine");

    public static PostgreSQLContainer<?> getContainer() {
        return postgres;
    }
}
