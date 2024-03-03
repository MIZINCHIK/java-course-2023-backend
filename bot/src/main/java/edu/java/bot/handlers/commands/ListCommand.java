package edu.java.bot.handlers.commands;

import com.pengrad.telegrambot.model.Message;
import edu.java.bot.PrimaveraBot;
import edu.java.model.dto.LinkResponse;
import edu.java.model.storage.LinkStorage;
import edu.java.model.storage.UserStorage;
import java.net.URI;
import java.util.List;
import static edu.java.bot.handlers.MessageFormatter.buildLinks;

public class ListCommand extends Command {
    public static final String NAME = "list";
    private static final String DESCRIPTION = "List the links being tracked. No arguments.";
    private static final String EMPTY_TRACKING_LIST = "No links being tracked.";
    private final LinkStorage linkStorage;
    private final UserStorage userStorage;

    public ListCommand(PrimaveraBot bot, LinkStorage linkStorage, UserStorage userStorage) {
        super(NAME, DESCRIPTION, bot);
        this.linkStorage = linkStorage;
        this.userStorage = userStorage;
    }

    @Override
    public void handle(Message message, String[] args) {
        if (!userStorage.isUserRegistered(message.from().id())) {
            handleUserNotRegistered(message);
        } else {
            List<URI> tracked =
                linkStorage.getLinksByUserId(message.from().id()).stream().map(LinkResponse::url).toList();
            if (tracked.isEmpty()) {
                bot.respond(message.chat().id(), message.messageId(), EMPTY_TRACKING_LIST);
            } else {
                bot.respondMd(message.chat().id(), message.messageId(), buildLinks(tracked));
            }
        }
    }
}
