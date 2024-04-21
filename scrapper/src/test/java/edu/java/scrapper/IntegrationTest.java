package edu.java.scrapper;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import liquibase.Scope;
import liquibase.UpdateSummaryEnum;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionCommandStep;
import liquibase.command.core.helpers.ShowSummaryArgument;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.DirectoryResourceAccessor;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DirtiesContext
@SpringBootTest(properties = "spring.kafka.use-queue=false")
public abstract class IntegrationTest {
    @Container
    public static PostgreSQLContainer<?> POSTGRES;
    public static Database database;

    static {
        POSTGRES = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("scrapper")
            .withUsername("postgres")
            .withPassword("postgres");
        POSTGRES.start();
    }

    @BeforeAll
    public static void runMigrations() {
        try {
            JdbcDatabaseContainer<?> c = POSTGRES;
            Connection connection = DriverManager.getConnection(c.getJdbcUrl(), c.getUsername(), c.getPassword());
            database =
                DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Path changelog = Path.of(System.getProperty("user.dir")).getParent()
                .resolve("scrapper")
                .resolve("src")
                .resolve("main")
                .resolve("resources")
                .resolve("migrations");
            Scope.child(Scope.Attr.resourceAccessor, new DirectoryResourceAccessor(changelog), () -> {
                new CommandScope(UpdateCommandStep.COMMAND_NAME)
                    .addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, database)
                    .addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, "master.xml")
                    .addArgumentValue(ShowSummaryArgument.SHOW_SUMMARY, UpdateSummaryEnum.SUMMARY)
                    .execute();
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }
}
