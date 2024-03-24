package edu.java.scrapper.dto;

import edu.java.model.links.LinkDomain;
import java.time.OffsetDateTime;

public record LinkDto(long id, String url, LinkDomain service, OffsetDateTime lastUpdate) {
}
