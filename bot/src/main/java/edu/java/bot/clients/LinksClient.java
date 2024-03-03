package edu.java.bot.clients;

import edu.java.model.dto.AddLinkRequest;
import edu.java.model.dto.LinkResponse;
import edu.java.model.dto.ListLinksResponse;
import edu.java.model.dto.RemoveLinkRequest;
import java.net.URI;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/links")
public interface LinksClient {
    @GetExchange
    ListLinksResponse getLinks(@RequestHeader("Tg-Chat-Id") Long tgChatId);

    @PostExchange
    LinkResponse postLink(@RequestHeader("Tg-Chat-Id") Long tgChatId, @RequestBody AddLinkRequest addLinkRequest);

    default LinkResponse postLink(Long tgChatId, URI link) {
        return postLink(tgChatId, new AddLinkRequest(link));
    }

    @DeleteExchange
    LinkResponse deleteLink(
        @RequestHeader("Tg-Chat-Id") Long tgChatId,
        @RequestBody RemoveLinkRequest removeLinkRequest
    );

    default LinkResponse deleteLink(Long tgChatId, URI link) {
        return deleteLink(tgChatId, new RemoveLinkRequest(link));
    }
}
