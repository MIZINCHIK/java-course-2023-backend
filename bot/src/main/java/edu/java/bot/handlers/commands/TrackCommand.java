package edu.java.bot.handlers.commands;

import com.pengrad.telegrambot.model.Message;
import edu.java.bot.PrimaveraBot;
import edu.java.model.links.Link;
import edu.java.model.storage.LinkStorage;
import edu.java.model.storage.UserStorage;
import static edu.java.model.links.Link.isLinkCorrect;

public class TrackCommand extends Command {
    public static final String NAME = "track";
    private static final String DESCRIPTION = "Track a link. /track <link1> ...";
    private final UserStorage userStorage;
    private final LinkStorage linkStorage;

    public TrackCommand(PrimaveraBot bot, UserStorage userStorage, LinkStorage linkStorage) {
        super(NAME, DESCRIPTION, bot);
        this.userStorage = userStorage;
        this.linkStorage = linkStorage;
    }

    @Override
    public void handle(Message message, String[] args) {
        int argsLength = args.length;
        Long userId = message.from().id();
        if (argsLength < 2) {
            handleIllegalArguments(message);
        } else if (!userStorage.isUserRegistered(userId)) {
            handleUserNotRegistered(message);
        } else {
            for (int i = 1; i < argsLength; i++) {
                if (!isLinkCorrect(args[i])) {
                    handleLinkUnsupported(args[i], message);
                } else {
                    linkStorage.trackLink(new Link(args[i]), userId);
                    handleSuccess(message);
                }
            }
        }
    }

    private void handleLinkUnsupported(String link, Message message) {
        bot.respond(message.chat().id(), message.messageId(), "The following link is unsupported: " + link);
    }
}
