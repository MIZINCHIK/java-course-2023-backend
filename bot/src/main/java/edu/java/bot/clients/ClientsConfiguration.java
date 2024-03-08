package edu.java.bot.clients;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@Log4j2
public class ClientsConfiguration {
    private static final String LINKS_ERROR = "Links client encountered and error";
    private static final String TG_ERROR = "Tg chat client encountered and error";

    @Bean
    public LinksClient linksClient(
        WebClient.Builder builder,
        @Value("${clients.scrapper.base-url:http://localhost:8080/}") String baseUrl
    ) {
        builder.defaultStatusHandler(HttpStatusCode::isError, resp -> {
            throw new HttpClientErrorException(resp.statusCode(), LINKS_ERROR);
        });
        return buildFactory(baseUrl, builder).createClient(LinksClient.class);
    }

    @Bean
    public TgChatClient tgChatClient(
        WebClient.Builder builder,
        @Value("${clients.scrapper.base-url:http://localhost:8080/}") String baseUrl
    ) {
        builder.defaultStatusHandler(HttpStatusCode::isError, resp -> {
            log.error(resp.headers());
            throw new HttpClientErrorException(resp.statusCode(), TG_ERROR);
        });
        return buildFactory(baseUrl, builder).createClient(TgChatClient.class);
    }

    private HttpServiceProxyFactory buildFactory(String baseUrl, WebClient.Builder builder) {
        WebClient webClient = builder.baseUrl(baseUrl).build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        return HttpServiceProxyFactory.builderFor(adapter).build();
    }
}

