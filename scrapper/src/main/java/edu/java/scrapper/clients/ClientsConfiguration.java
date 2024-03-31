package edu.java.scrapper.clients;

import edu.java.backoff.Backoff;
import edu.java.backoff.BackoffType;
import java.time.Duration;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import static edu.java.backoff.ClientUtils.getClient;

@Configuration
@Log4j2
public class ClientsConfiguration {
    private static final String GITHUB_ERROR = "Github client encountered and error";
    private static final String STACK_OVERFLOW_ERROR = "StackOverflow client encountered and error";
    private static final String BOT_ERROR = "Bot client encountered and error";

    @Bean
    public GitHubClient gitHubClient(
        WebClient.Builder builder,
        @Value("${clients.github.base-url:https://api.github.com/}") String baseUrl,
        @Value("${clients.github.backoff.type:exponential}") BackoffType backoffType,
        @Value("${clients.github.backoff.min-backoff:1s}") Duration minBackoff,
        @Value("${clients.github.backoff.max-attempts:5}") Integer maxAttempts,
        @Value("${clients.github.backoff.codes:}") List<Integer> codes
    ) {
        return getClient(
            GitHubClient.class,
            builder,
            GITHUB_ERROR,
            baseUrl,
            new Backoff(backoffType, minBackoff, maxAttempts, codes)
        );
    }

    @Bean
    public StackOverflowClient stackOverflowClient(
        WebClient.Builder builder,
        @Value("${clients.stack-overflow.base-url:https://api.stackexchange.com/}") String baseUrl,
        @Value("${clients.stack-overflow.backoff.type:exponential}") BackoffType backoffType,
        @Value("${clients.stack-overflow.backoff.min-backoff:1s}") Duration minBackoff,
        @Value("${clients.stack-overflow.backoff.max-attempts:5}") Integer maxAttempts,
        @Value("${clients.stack-overflow.backoff.codes:}") List<Integer> codes
    ) {
        return getClient(
            StackOverflowClient.class,
            builder,
            STACK_OVERFLOW_ERROR,
            baseUrl,
            new Backoff(backoffType, minBackoff, maxAttempts, codes)
        );
    }

    @Bean
    public BotClient botClient(
        WebClient.Builder builder,
        @Value("${clients.bot.base-url:http://localhost:8090/}") String baseUrl,
        @Value("${clients.bot.backoff.type:exponential}") BackoffType backoffType,
        @Value("${clients.bot.backoff.min-backoff:1s}") Duration minBackoff,
        @Value("${clients.bot.backoff.max-attempts:5}") Integer maxAttempts,
        @Value("${clients.bot.backoff.codes:}") List<Integer> codes

    ) {
        return getClient(
            BotClient.class,
            builder,
            BOT_ERROR,
            baseUrl,
            new Backoff(backoffType, minBackoff, maxAttempts, codes)
        );
    }
}
