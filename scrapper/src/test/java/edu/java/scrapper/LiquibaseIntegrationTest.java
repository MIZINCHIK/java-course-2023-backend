package edu.java.scrapper;

import liquibase.exception.DatabaseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LiquibaseIntegrationTest extends IntegrationTest {
    @Test
    void test() throws DatabaseException {
        Assertions.assertTrue(POSTGRES.isRunning());
        Assertions.assertFalse(database.getConnection().isClosed());
    }
}
