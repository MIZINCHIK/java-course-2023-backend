package edu.java.bot.storage;

import edu.java.bot.clients.LinksClient;
import edu.java.model.dto.LinkResponse;
import edu.java.model.links.Link;
import edu.java.model.storage.LinkStorage;
import java.net.URISyntaxException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;

@Repository
@RequiredArgsConstructor
public class WebLinkStorage implements LinkStorage {
    private final LinksClient linksClient;

    @Override
    public List<LinkResponse> getLinksByUserId(long userId) {
        return linksClient.getLinks(userId).links();
    }

    @Override
    public long trackLink(Link link, long userId) throws HttpClientErrorException {
        try {
            return linksClient.postLink(userId, link.getUrl().toURI()).id();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long untrackLink(Link link, long userId) throws HttpClientErrorException {
        try {
            return linksClient.deleteLink(userId, link.getUrl().toURI()).id();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isLinkTracked(Link link, long userId) throws HttpClientErrorException {
        return linksClient.getLinks(userId).links().stream()
            .map(LinkResponse::url)
            .anyMatch(x -> {
                try {
                    return x.equals(link.getUrl().toURI());
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }
}
