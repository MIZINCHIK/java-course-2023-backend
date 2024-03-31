package edu.java.backoff;

import java.time.Duration;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Log4j2
public class LinearRetry extends Retry {
    private final int maxAttempts;
    private final Duration durationStep;
    private Duration currentBackoff;

    public LinearRetry(int maxAttempts, Duration minBackoff) {
        this.maxAttempts = maxAttempts;
        durationStep = minBackoff;
        currentBackoff = Duration.ZERO;
    }

    @Override
    public Publisher<?> generateCompanion(Flux<RetrySignal> retrySignals) {
        return retrySignals.flatMap(this::getRetry);
    }

    private Mono<Long> getRetry(Retry.RetrySignal rs) {
        if (rs.totalRetries() < maxAttempts) {
            currentBackoff = currentBackoff.plus(durationStep);
            log.info("Retry {} with backoff {} sec", rs.totalRetries(), currentBackoff.toSeconds());
            return Mono.delay(currentBackoff)
                .thenReturn(rs.totalRetries());
        } else {
            log.info("Retries exhausted with error: {}", rs.failure().getMessage());
            throw Exceptions.propagate(rs.failure());
        }
    }
}
