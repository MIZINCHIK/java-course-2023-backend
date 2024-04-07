package edu.java.scrapper.configuration;

import edu.java.model.kafka.TopicConfiguration;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotNull
    @Bean
    Scheduler scheduler,
    @NotNull
    @Bean
    AccessType databaseAccessType,
    @Bean
    boolean useQueue,
    @NotNull
    @Bean
    Kafka kafka
) {
    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
    }

    public enum AccessType {
        JDBC, JPA, JOOQ
    }

    public record Kafka(Topics topics) {}

    public record Topics(TopicConfiguration updates) {}
}
