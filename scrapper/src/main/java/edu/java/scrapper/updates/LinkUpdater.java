package edu.java.scrapper.updates;

import edu.java.model.dto.LinkUpdate;
import edu.java.model.exceptions.MalformedUrlException;
import edu.java.scrapper.clients.BotClient;
import edu.java.scrapper.clients.GitHubClient;
import edu.java.scrapper.clients.StackOverflowClient;
import edu.java.scrapper.clients.updates.github.Commit;
import edu.java.scrapper.clients.updates.stackoverflow.StackOverflowAnswer;
import edu.java.scrapper.dto.LinkDto;
import edu.java.scrapper.exceptions.IncorrectStackoverflowIdsParameter;
import edu.java.scrapper.service.ModifiableLinkStorage;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import static edu.java.scrapper.updates.UpdateDescriptionFormatter.formatNewAnswerMessage;
import static edu.java.scrapper.updates.UpdateDescriptionFormatter.formatNewCommitMessage;
import static edu.java.scrapper.updates.UpdateDescriptionFormatter.formatNewUpdateMessage;

@Log4j2
@Component
@RequiredArgsConstructor
public class LinkUpdater {
    private final StackOverflowClient stackOverflowClient;
    private final GitHubClient gitHubClient;
    private final BotClient botClient;
    private final ModifiableLinkStorage linkService;

    public void checkLinks(List<LinkDto> links) {
        links.forEach(this::checkLink);
    }

    private void checkLink(LinkDto linkDto) {
        OffsetDateTime prior = OffsetDateTime.now(ZoneOffset.UTC);
        try {
            switch (linkDto.service()) {
                case STACKOVERFLOW -> sendUpdateStackoverflow(linkDto, prior);
                case GITHUB -> sendUpdateGithub(linkDto, prior);
                case null, default -> log.error("");
            }
        } catch (MalformedUrlException ignored) {
            linkService.removeLink(linkDto.url());
        } catch (IncorrectStackoverflowIdsParameter ignored) {
        }
    }

    private void sendUpdateStackoverflow(LinkDto linkDto, OffsetDateTime prior) throws MalformedUrlException {
        String url = linkDto.url();
        URI uri;
        try {
            uri = URI.create(linkDto.url());
        } catch (IllegalArgumentException e) {
            throw new MalformedUrlException(e);
        }
        OffsetDateTime lastActivityDate = stackOverflowClient.getUpdateByUrl(url).lastActivityDate();
        List<StackOverflowAnswer> relevantAnswers = stackOverflowClient.getAnswerListByUrl(url).stream()
            .filter(answer -> answer.creationDate().isAfter(linkDto.lastUpdate()))
            .sorted(Comparator.comparing(StackOverflowAnswer::creationDate))
            .toList();
        OffsetDateTime lastAnswerDate =
            relevantAnswers.isEmpty() ? lastActivityDate : relevantAnswers.getLast().creationDate();
        if (!updateLinkCheckUpDate(lastActivityDate, lastAnswerDate, prior, linkDto).isAfter(linkDto.lastUpdate())) {
            return;
        }
        List<Long> chats = linkService.getUsersByLink(linkDto.id());
        botClient.sendUpdate(
            new LinkUpdate(
                linkDto.id(),
                uri,
                formatNewUpdateMessage(lastActivityDate),
                chats
            ));
        for (StackOverflowAnswer answer : relevantAnswers) {
            botClient.sendUpdate(
                new LinkUpdate(
                    linkDto.id(),
                    uri,
                    formatNewAnswerMessage(answer),
                    chats
                )
            );
        }
    }

    private void sendUpdateGithub(LinkDto linkDto, OffsetDateTime prior) throws MalformedUrlException {
        String url = linkDto.url();
        URI uri;
        try {
            uri = URI.create(linkDto.url());
        } catch (IllegalArgumentException e) {
            throw new MalformedUrlException(e);
        }
        OffsetDateTime lastActivityDate = gitHubClient.getUpdate(url).updatedAt();
        List<Commit> commits = gitHubClient.getCommits(url).stream()
            .filter(commit -> commit.info().author().date().isAfter(linkDto.lastUpdate()))
            .sorted(Comparator.comparing(o -> o.info().author().date()))
            .toList();
        OffsetDateTime lastCommitDate =
            commits.isEmpty() ? lastActivityDate : commits.getLast().info().author().date();
        if (!updateLinkCheckUpDate(lastActivityDate, lastCommitDate, prior, linkDto).isAfter(linkDto.lastUpdate())) {
            return;
        }
        List<Long> chats = linkService.getUsersByLink(linkDto.id());
        botClient.sendUpdate(
            new LinkUpdate(
                linkDto.id(),
                uri,
                formatNewUpdateMessage(lastActivityDate),
                chats
            ));
        for (Commit commit : commits) {
            botClient.sendUpdate(
                new LinkUpdate(
                    linkDto.id(),
                    uri,
                    formatNewCommitMessage(commit),
                    chats
                )
            );
        }
    }

    private OffsetDateTime updateLinkCheckUpDate(
        OffsetDateTime lastActivityDate,
        OffsetDateTime lastHandledEventDate,
        OffsetDateTime priorDate,
        LinkDto linkDto
    ) {
        OffsetDateTime lastObservedEventDate =
            lastActivityDate.isAfter(lastHandledEventDate) ? lastActivityDate : lastHandledEventDate;
        linkService.updateLink(
            linkDto.id(),
            priorDate.isBefore(lastObservedEventDate) ? lastObservedEventDate : priorDate
        );
        return lastObservedEventDate;
    }
}
