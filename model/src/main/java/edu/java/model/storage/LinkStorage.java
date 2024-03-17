package edu.java.model.storage;

import edu.java.model.dto.LinkResponse;
import edu.java.model.links.Link;
import java.util.List;

public interface LinkStorage {
    List<LinkResponse> getLinksByUserId(long userId);

    long trackLink(Link link, long userId);

    long untrackLink(Link link, long userId);

    boolean isLinkTracked(Link link, long userId);
}
