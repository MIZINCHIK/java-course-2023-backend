package edu.java.scrapper.scheduling;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Log4j2
@Configuration
@EnableScheduling
@ConditionalOnProperty(value = "app.scheduler.enable", havingValue = "true")
public class LinkUpdaterScheduler {
    private static final String STUB = "I'm scheduled \uD83C\uDF48 \uD83D\uDC04 \uD83D\uDC33";

    @Scheduled(fixedDelayString = "#{@scheduler.interval}")
    public void update() {
        log.info(STUB);
    }
}
