package edu.java.scrapper.updates;

import edu.java.model.dto.LinkUpdate;
import edu.java.scrapper.clients.BotClient;
import edu.java.scrapper.clients.GitHubClient;
import edu.java.scrapper.clients.StackOverflowClient;
import edu.java.scrapper.clients.updates.GithubUpdate;
import edu.java.scrapper.clients.updates.StackOverflowUpdate;
import edu.java.scrapper.dto.LinkDto;
import edu.java.scrapper.service.ModifiableLinkStorage;
import java.net.URI;
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
        switch (linkDto.service()) {
            case SOF -> sendStackOverflowUpdate(stackOverflowClient.getUpdateByUrl(linkDto.url()), linkDto);
            case GITHUB -> sendGitHubUpdate(gitHubClient.getUpdate(linkDto.url()), linkDto);
            case null, default -> log.error("");
        }
    }

    private void sendStackOverflowUpdate(StackOverflowUpdate update, LinkDto linkDto) {
        if (!update.lastActivityDate().isAfter(linkDto.lastUpdate())) {
            return;
        }
        linkStorage.updateLink(linkDto.id());
        botClient.sendUpdate(
            new LinkUpdate(
                linkDto.id(),
                URI.create(linkDto.url()),
                "Last activity date: " + update.lastActivityDate(),
                linkStorage.getUsersByLink(linkDto.id())
            ));
    }

    private void sendGitHubUpdate(GithubUpdate update, LinkDto linkDto) {
        if (!update.updatedAt().isAfter(linkDto.lastUpdate())) {
            return;
        }
        linkStorage.updateLink(linkDto.id());
        botClient.sendUpdate(
            new LinkUpdate(
                linkDto.id(),
                URI.create(linkDto.url()),
                "Last updated at: " + update.updatedAt(),
                linkStorage.getUsersByLink(linkDto.id())
            ));
    }
}
