package edu.java.scrapper.clients.updates.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URL;
import java.time.OffsetDateTime;

public record Commit(@JsonProperty("html_url") URL url, @JsonProperty("commit") Info info) {
    public record Info(Author author) {
        public record Author(String name, OffsetDateTime date) {
        }
    }
}
