package edu.java.scrapper.storage;

import edu.java.model.dto.LinkResponse;
import edu.java.model.links.Link;
import edu.java.model.storage.LinkStorage;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class DbLinkStorage implements LinkStorage {
    @Override
    public List<LinkResponse> getLinksByUserId(Long userId) {
        //TODO
        return List.of();
    }

    @Override
    public Long trackLink(Link link, Long userId) {
        //TODO
        return 0L;
    }

    @Override
    public Long untrackLink(Link link, Long userId) {
        //TODO
        return 0L;
    }

    @Override
    public boolean isLinkTracked(Link link, Long userId) {
        //TODO
        return true;
    }
}
