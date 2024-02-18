package edu.java.bot.storage;

import edu.java.bot.links.Link;
import java.net.URL;
import java.util.List;

public class DbStorage implements Storage {
    @Override
    public List<URL> getLinksByUserId(Long userId) {
        //TODO
        return List.of();
    }

    @Override
    public void trackLink(Link link, Long userId) {
        //TODO
    }

    @Override
    public void untrackLink(Link link, Long userId) {
        //TODO
    }

    @Override
    public void registerUser(Long userId) {
        //TODO
    }

    @Override
    public boolean isUserRegistered(Long userId) {
        return true;
    }

    @Override
    public boolean isLinkTracked(Link link) {
        //TODO
        return true;
    }
}
