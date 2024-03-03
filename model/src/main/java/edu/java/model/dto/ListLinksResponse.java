package edu.java.model.dto;

import java.util.List;

public record ListLinksResponse(List<LinkResponse> links, int size) {
}
