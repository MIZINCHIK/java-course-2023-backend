package edu.java.scrapper.updates.scheduling;

import edu.java.scrapper.service.ModifiableLinkStorage;
import edu.java.scrapper.updates.LinkUpdater;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Log4j2
@Configuration
@EnableScheduling
@ConditionalOnProperty(value = "app.scheduler.enable", havingValue = "true")
@RequiredArgsConstructor
public class LinkUpdaterScheduler {
    private static final String STUB = "I'm scheduled \uD83C\uDF48 \uD83D\uDC04 \uD83D\uDC33";
    private final ModifiableLinkStorage linkService;
    private final LinkUpdater linkUpdater;
    @Value(value = "#{@scheduler.forceCheckDelay()}")
    private Duration expirationInterval;

    @Scheduled(fixedDelayString = "#{@scheduler.interval}")
    public void update() {
        log.info(STUB);
        linkUpdater.checkLinks(linkService.getLinksWithExpiredCheckTime(expirationInterval));
    }
}
