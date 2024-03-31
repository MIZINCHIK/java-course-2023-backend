package edu.java.backoff;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class LinearRetryTest {
    @Test
    @DisplayName("Testing linear retry")
    void getRetry_whenGivenConfiguration_thenCorrect() {
        StepVerifier.withVirtualTime(() ->
                process()
                    .retryWhen(new LinearRetry(5, Duration.of(1, ChronoUnit.SECONDS))
                    )
            )
            .expectSubscription()
            .expectNoEvent(Duration.ofSeconds(1))
            .expectNoEvent(Duration.ofSeconds(2))
            .expectNoEvent(Duration.ofSeconds(3))
            .expectNoEvent(Duration.ofSeconds(4))
            .expectNoEvent(Duration.ofSeconds(5))
            .expectError()
            .verify();
    }

    private Mono<?> process() {
        return Mono.error(new RuntimeException("oops"));
    }
}
