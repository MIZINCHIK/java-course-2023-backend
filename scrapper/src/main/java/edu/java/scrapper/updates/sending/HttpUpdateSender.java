package edu.java.scrapper.updates.sending;

import edu.java.model.dto.LinkUpdate;
import edu.java.scrapper.clients.BotClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "use-queue", havingValue = "false")
public class HttpUpdateSender implements UpdateSender {
    private final BotClient botClient;

    @Override
    public void sendUpdate(LinkUpdate update) {
        botClient.sendUpdate(update);
    }
}
