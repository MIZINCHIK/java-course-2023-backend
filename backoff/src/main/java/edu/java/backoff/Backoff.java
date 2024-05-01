package edu.java.backoff;

import java.time.Duration;
import java.util.List;

public record Backoff(BackoffType type, Duration minBackoff, Integer maxAttempts, List<Integer> codes) {
}
