package edu.java.model.storage;

import edu.java.model.dto.LinkResponse;
import edu.java.model.links.Link;
import java.util.List;

public interface LinkStorage {
    List<LinkResponse> getLinksByUserId(Long userId);

    Long trackLink(Link link, Long userId);

    Long untrackLink(Link link, Long userId);

    boolean isLinkTracked(Link link, Long userId);
}
