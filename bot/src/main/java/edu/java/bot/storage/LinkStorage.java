package edu.java.bot.storage;

import edu.java.bot.links.Link;
import java.net.URL;
import java.util.List;

public interface LinkStorage {
    List<URL> getLinksByUserId(Long userId);

    void trackLink(Link link, Long userId);

    void untrackLink(Link link, Long userId);

    boolean isLinkTracked(Link link);
}
