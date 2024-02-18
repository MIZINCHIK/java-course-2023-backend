package edu.java.bot.storage;

import edu.java.bot.links.Link;
import java.net.URL;
import java.util.List;

public interface Storage {
    List<URL> getLinksByUserId(Long userId);
    void trackLink(Link link, Long userId);
    void untrackLink(Link link, Long userId);
    void registerUser(Long userId);
    boolean isUserRegistered(Long userId);
    boolean isLinkTracked(Link link);
}
