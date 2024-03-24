package edu.java.model.links;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public enum LinkDomain {
    SOF("StackOverflow"),
    GITHUB("GitHub"),
    UNSUPPORTED("Not supported");

    public final String name;

    LinkDomain(String name) {
        this.name = name;
    }

    private static final Map<String, LinkDomain> MAP;

    static {
        MAP = new HashMap<>();
        for (LinkDomain domain : values()) {
            MAP.put(domain.name, domain);
        }
    }

    public static LinkDomain of(String name) {
        return MAP.get(name);
    }

    public static LinkDomain inferDomain(URL url) {
        return switch (url.getHost()) {
            case "github.com", "www.github.com" -> GITHUB;
            case "stackoverflow.com", "www.stackoverflow.com" -> SOF;
            default -> UNSUPPORTED;
        };
    }
}
