package edu.java.scrapper.clients;

import edu.java.scrapper.configuration.ApplicationConfig;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@AllArgsConstructor
public class ClientsConfiguration {
    private static final String GITHUB_ERROR = "Github client encountered and error";
    private static final String STACK_OVERFLOW_ERROR = "StackOverflow client encountered and error";
    private static final String STACK_OVERFLOW_DEFAULT_BASE_URL = "https://api.stackexchange.com/";
    private static final String GITHUB_DEFAULT_BASE_URL = "https://api.github.com/";

    @Bean
    public GitHubClient gitHubClient(WebClient.Builder builder, ApplicationConfig config) {
        builder.defaultStatusHandler(HttpStatusCode::isError, resp -> {
            throw new HttpClientErrorException(resp.statusCode(), GITHUB_ERROR);
        });
        ApplicationConfig.Github github = config.github();
        String baseUrl = github == null ? STACK_OVERFLOW_DEFAULT_BASE_URL : github.baseUrl();
        return buildFactory(config.github().baseUrl(), builder).createClient(GitHubClient.class);
    }

    @Bean
    public StackOverflowClient stackOverflowClient(WebClient.Builder builder, ApplicationConfig config) {
        builder.defaultStatusHandler(HttpStatusCode::isError, resp -> {
            throw new HttpClientErrorException(resp.statusCode(), STACK_OVERFLOW_ERROR);
        });
        ApplicationConfig.StackOverflow stackOverflow = config.stackOverflow();
        String baseUrl = stackOverflow == null ? STACK_OVERFLOW_DEFAULT_BASE_URL : stackOverflow.baseUrl();
        return buildFactory(baseUrl, builder).createClient(StackOverflowClient.class);

    }

    public static HttpServiceProxyFactory buildFactory(String baseUrl, WebClient.Builder builder) {
        WebClient webClient = builder.baseUrl(baseUrl).build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        return HttpServiceProxyFactory.builderFor(adapter).build();
    }
}
