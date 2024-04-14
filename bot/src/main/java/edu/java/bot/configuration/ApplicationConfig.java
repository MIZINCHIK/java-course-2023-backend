package edu.java.bot.configuration;

import edu.java.bot.PrimaveraBot;
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
    ScrapperTopic scrapperTopic
) {
    @Bean
    PrimaveraBot getBot() {
        return new PrimaveraBot(telegramToken);
    }

    public record ScrapperTopic(@NotEmpty String name) {
    }
}
