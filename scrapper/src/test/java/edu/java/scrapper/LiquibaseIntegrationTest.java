package edu.java.scrapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LiquibaseIntegrationTest extends IntegrationTest {
    @Test
    void test() {
        Assertions.assertTrue(POSTGRES.isRunning());
        System.out.println(POSTGRES.getJdbcUrl());
    }
}
