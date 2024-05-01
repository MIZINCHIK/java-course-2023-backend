package edu.java.backoff;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public class ClientUtils {
    private ClientUtils() {
        throw new IllegalStateException();
    }

    public static <T> T getClient(
        Class<T> clazz, WebClient.Builder builder, String error, String baseUrl, Backoff backoff
    ) {
        builder.defaultStatusHandler(HttpStatusCode::isError, resp -> {
            throw new HttpClientErrorException(resp.statusCode(), error);
        });
        return buildFactory(baseUrl, builder, backoff).createClient(
            clazz);
    }

    private static HttpServiceProxyFactory buildFactory(
        String baseUrl,
        WebClient.Builder builder,
        Backoff backoff
    ) {
        WebClient webClient = builder.baseUrl(baseUrl).filter(withRetryableRequests(backoff)).build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        return HttpServiceProxyFactory.builderFor(adapter).build();
    }

    private static ExchangeFilterFunction withRetryableRequests(Backoff backoff) {
        return (request, next) -> next.exchange(request)
            .flatMap(clientResponse -> Mono.just(clientResponse)
                .filter(response -> backoff.codes().contains(clientResponse.statusCode().value()))
                .flatMap(response -> clientResponse.createException())
                .flatMap(Mono::error)
                .thenReturn(clientResponse))
            .retryWhen(retryBackoffSpec(backoff));
    }

    private static Retry retryBackoffSpec(Backoff backoff) {
        return switch (backoff.type()) {
            case EXPONENTIAL -> Retry.backoff(backoff.maxAttempts(), backoff.minBackoff());
            case CONSTANT -> Retry.fixedDelay(backoff.maxAttempts(), backoff.minBackoff());
            case LINEAR -> new LinearRetry(backoff.maxAttempts(), backoff.minBackoff());
            case null -> null;
        };
    }
}
