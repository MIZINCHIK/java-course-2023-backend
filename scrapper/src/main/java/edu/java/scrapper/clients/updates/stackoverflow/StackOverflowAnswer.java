package edu.java.scrapper.clients.updates.stackoverflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URL;
import java.time.OffsetDateTime;

public record StackOverflowAnswer(Owner owner, @JsonProperty("creation_date") OffsetDateTime creationDate, @JsonProperty("answer_id") Long answerId) {
    public record Owner(@JsonProperty("user_id") Long userId, @JsonProperty("display_name") String name, URL link) {
    }
}
