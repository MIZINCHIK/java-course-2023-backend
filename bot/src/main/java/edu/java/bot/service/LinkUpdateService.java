package edu.java.bot.service;

import edu.java.bot.PrimaveraBot;
import edu.java.model.dto.LinkUpdate;
import edu.java.model.exceptions.MalformedUrlException;
import edu.java.model.links.Link;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import static edu.java.bot.handlers.MessageFormatter.formUpdateMessage;

@Service
@RequiredArgsConstructor
public class LinkUpdateService {
    private final PrimaveraBot bot;

    public void processUpdate(LinkUpdate update) {
        try {
            Link ignored = new Link(update.url().toString());
        } catch (Exception e) {
            throw new MalformedUrlException();
        }
        for (Long chatId : update.tgChatIds()) {
            bot.message(chatId, formUpdateMessage(update));
        }
    }
}
