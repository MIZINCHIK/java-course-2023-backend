package edu.java.scrapper.updates;

import edu.java.model.dto.LinkUpdate;
import edu.java.scrapper.clients.BotClient;
import edu.java.scrapper.clients.GitHubClient;
import edu.java.scrapper.clients.StackOverflowClient;
import edu.java.scrapper.dto.LinkDto;
import edu.java.scrapper.service.ModifiableLinkStorage;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class LinkUpdater {
    private final StackOverflowClient stackOverflowClient;
    private final GitHubClient gitHubClient;
    private final BotClient botClient;
    private final ModifiableLinkStorage linkStorage;

    public void checkLinks(List<LinkDto> links) {
        links.forEach(this::checkLink);
    }

    private void checkLink(LinkDto linkDto) {
        OffsetDateTime prior = OffsetDateTime.now(ZoneOffset.UTC);
        switch (linkDto.service()) {
            case SOF ->
                sendUpdate(linkDto, stackOverflowClient.getUpdateByUrl(linkDto.url()).lastActivityDate(), prior);
            case GITHUB -> sendUpdate(linkDto, gitHubClient.getUpdate(linkDto.url()).updatedAt(), prior);
            case null, default -> log.error("");
        }
    }

    private void sendUpdate(LinkDto linkDto, OffsetDateTime updateTime, OffsetDateTime prior) {
        if (!updateTime.isAfter(linkDto.lastUpdate())) {
            return;
        }
        linkStorage.updateLink(linkDto.id(), prior.isAfter(updateTime) ? updateTime : prior);
        botClient.sendUpdate(
            new LinkUpdate(
                linkDto.id(),
                URI.create(linkDto.url()),
                "Last updated at: " + updateTime,
                linkStorage.getUsersByLink(linkDto.id())
            ));
    }
}
