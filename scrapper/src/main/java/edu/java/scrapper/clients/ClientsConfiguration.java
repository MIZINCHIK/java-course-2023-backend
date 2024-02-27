package edu.java.scrapper.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ClientsConfiguration {
    private static final String GITHUB_ERROR = "Github client encountered and error";
    private static final String STACK_OVERFLOW_ERROR = "StackOverflow client encountered and error";

    @Bean
    public GitHubClient gitHubClient(
        WebClient.Builder builder, @Value("${github.base-url:https://api.github.com/}") String baseUrl
    ) {
        builder.defaultStatusHandler(HttpStatusCode::isError, resp -> {
            throw new HttpClientErrorException(resp.statusCode(), GITHUB_ERROR);
        });
        return buildFactory(baseUrl, builder).createClient(GitHubClient.class);
    }

    @Bean
    public StackOverflowClient stackOverflowClient(
        WebClient.Builder builder,
        @Value("${stack-overflow.base-url:https://api.stackexchange.com/}") String baseUrl
    ) {
        builder.defaultStatusHandler(HttpStatusCode::isError, resp -> {
            throw new HttpClientErrorException(resp.statusCode(), STACK_OVERFLOW_ERROR);
        });
        return buildFactory(baseUrl, builder).createClient(StackOverflowClient.class);

    }

    private HttpServiceProxyFactory buildFactory(String baseUrl, WebClient.Builder builder) {
        WebClient webClient = builder.baseUrl(baseUrl).build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        return HttpServiceProxyFactory.builderFor(adapter).build();
    }
}
