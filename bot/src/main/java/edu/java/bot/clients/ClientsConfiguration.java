package edu.java.bot.clients;

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
    private static final String LINKS_ERROR = "Links client encountered and error";
    private static final String TG_ERROR = "Tg chat client encountered and error";

    @Bean
    public LinksClient linksClient(
        WebClient.Builder builder,
        @Value("${clients.scrapper.base-url:http://localhost:8080/}") String baseUrl,
        @Value("${clients.scrapper.backoff.type:exponential}") BackoffType backoffType,
        @Value("${clients.scrapper.backoff.min-backoff:1s}") Duration minBackoff,
        @Value("${clients.scrapper.backoff.max-attempts:5}") Integer maxAttempts,
        @Value("${clients.scrapper.backoff.codes:}") List<Integer> codes
    ) {
        return getClient(
            LinksClient.class,
            builder,
            LINKS_ERROR,
            baseUrl,
            new Backoff(backoffType, minBackoff, maxAttempts, codes)
        );
    }

    @Bean
    public TgChatClient tgChatClient(
        WebClient.Builder builder,
        @Value("${clients.scrapper.base-url:http://localhost:8080/}") String baseUrl,
        @Value("${clients.scrapper.backoff.type:exponential}") BackoffType backoffType,
        @Value("${clients.scrapper.backoff.min-backoff:1s}") Duration minBackoff,
        @Value("${clients.scrapper.backoff.max-attempts:5}") Integer maxAttempts,
        @Value("${clients.scrapper.backoff.codes:}") List<Integer> codes
    ) {
        return getClient(
            TgChatClient.class,
            builder,
            TG_ERROR,
            baseUrl,
            new Backoff(backoffType, minBackoff, maxAttempts, codes)
        );
    }
}

