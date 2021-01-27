package nl.arnovanoort.stockreader.repository;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresqlTestContainer extends PostgreSQLContainer<PostgresqlTestContainer> {
    private static final String IMAGE_VERSION = "postgres:11.1";
    private static PostgresqlTestContainer container;

    private PostgresqlTestContainer() {
        super(IMAGE_VERSION);
    }

    public static PostgresqlTestContainer getInstance() {
        if (container == null) {
            container = new PostgresqlTestContainer().withDatabaseName("stocks-test");
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("db.url", container.getJdbcUrl().replace("jdbc", "r2dbc"));
        System.setProperty("db.username", container.getUsername());
        System.setProperty("db.password", container.getPassword());

        System.setProperty("spring.flyway.url", container.getJdbcUrl());
        System.setProperty("spring.flyway.user", container.getUsername());
        System.setProperty("spring.flyway.password", container.getPassword());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}