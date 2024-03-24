package edu.java.scrapper.clients.updates.stackoverflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record StackOverflowUpdate(@JsonProperty("last_activity_date") OffsetDateTime lastActivityDate) {
}
