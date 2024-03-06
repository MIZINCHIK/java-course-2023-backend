package edu.java.scrapper.clients.updates;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record StackOverflowUpdate(@JsonProperty("last_activity_date") OffsetDateTime lastActivityDate) {
}
