package edu.java.model.links;

import java.net.URL;

public enum LinkDomain {
    STACKOVERFLOW,
    GITHUB,
    UNSUPPORTED;

    public static LinkDomain inferDomain(URL url) {
        return switch (url.getHost()) {
            case "github.com", "www.github.com" -> GITHUB;
            case "stackoverflow.com", "www.stackoverflow.com" -> STACKOVERFLOW;
            default -> UNSUPPORTED;
        };
    }
}
