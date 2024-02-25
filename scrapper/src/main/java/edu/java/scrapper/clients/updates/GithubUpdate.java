package edu.java.scrapper.clients.updates;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record GithubUpdate(@JsonProperty("updated_at") OffsetDateTime updatedAt) {
}
