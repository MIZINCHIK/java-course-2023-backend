package edu.java.bot.handlers.commands;

import com.pengrad.telegrambot.model.Message;
import edu.java.bot.PrimaveraBot;
import edu.java.bot.links.Link;
import edu.java.bot.storage.LinkStorage;
import edu.java.bot.storage.UserStorage;
import static edu.java.bot.links.Link.isLinkCorrect;

public class UntrackCommand extends Command {
    public static final String NAME = "untrack";
    private static final String DESCRIPTION = "Stop tracking a link. /untrack <link1> ...";
    private static final String ISNT_TRACKED = "The link wasn't tracked already.";
    private final UserStorage userStorage;
    private final LinkStorage linkStorage;

    public UntrackCommand(PrimaveraBot bot, UserStorage userStorage, LinkStorage linkStorage) {
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
                if (isLinkCorrect(args[i])) {
                    Link link = new Link(args[i]);
                    if (linkStorage.isLinkTracked(link)) {
                        linkStorage.untrackLink(link, userId);
                        handleSuccess(message);
                    } else {
                        handleIsntTracked(message);
                    }
                }
            }
        }
    }

    private void handleIsntTracked(Message message) {
        bot.respond(message.chat().id(), message.messageId(), ISNT_TRACKED);
    }
}
