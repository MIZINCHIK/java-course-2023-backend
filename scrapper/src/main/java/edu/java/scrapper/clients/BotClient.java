package edu.java.scrapper.clients;

import edu.java.model.dto.LinkUpdate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

public interface BotClient {
    @PostExchange("/updates")
    void sendUpdate(@RequestBody LinkUpdate update);
}
