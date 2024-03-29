package edu.java.model.links;

import java.net.URL;

public enum LinkDomain {
    SOF("StackOverflow"),
    GITHUB("GitHub"),
    UNSUPPORTED("Not supported");

    public final String name;

    LinkDomain(String name) {
        this.name = name;
    }

    public static LinkDomain inferDomain(URL url) {
        return switch (url.getHost()) {
            case "github.com", "www.github.com" -> GITHUB;
            case "stackoverflow.com", "www.stackoverflow.com" -> SOF;
            default -> UNSUPPORTED;
        };
    }
}
