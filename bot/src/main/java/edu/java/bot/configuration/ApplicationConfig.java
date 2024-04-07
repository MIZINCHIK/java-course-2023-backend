package edu.java.bot.configuration;

import edu.java.bot.PrimaveraBot;
import edu.java.model.kafka.TopicConfiguration;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotEmpty
    String telegramToken,
    @NotNull
    ScrapperTopic scrapperTopic,
    @NotNull
    @Bean
    Kafka kafka
) {
    @Bean
    PrimaveraBot getBot() {
        return new PrimaveraBot(telegramToken);
    }

    public record ScrapperTopic(@NotEmpty String name) {
    }

    public record Kafka(
        @NotEmpty
        String bootstrapServers,
        @NotNull
        Topics topics
    ) {
    }

    public record Topics(TopicConfiguration updates, TopicConfiguration dlq){}
}
