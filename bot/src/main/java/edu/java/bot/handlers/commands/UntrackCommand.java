package edu.java.bot.handlers.commands;

import com.pengrad.telegrambot.model.Message;
import edu.java.bot.PrimaveraBot;
import edu.java.bot.links.Link;
import static edu.java.bot.links.Link.isLinkCorrect;

public class UntrackCommand extends Command {
    public static final String NAME = "/untrack";
    private static final String DESCRIPTION = "Stop tracking a link. /untrack <link1> ...";
    private static final String ISNT_TRACKED = "The link wasn't tracked already.";

    public UntrackCommand() {
        super(NAME, DESCRIPTION);
    }

    @Override
    public void handle(Message message, String[] args, PrimaveraBot bot) {
        int argsLength = args.length;
        Long userId = message.from().id();
        if (argsLength < 2) {
            handleIllegalArguments(message, bot);
        } else if (!bot.isUserRegistered(userId)) {
            handleUserNotRegistered(message, bot);
        } else {
            for (int i = 1; i < argsLength; i++) {
                if (isLinkCorrect(args[i])) {
                    Link link = new Link(args[i]);
                    if (bot.isLinkTracked(link)) {
                        bot.untrackLink(link, userId);
                        handleSuccess(message, bot);
                    } else {
                        handleIsntTracked(message, bot);
                    }
                }
            }
        }
    }

    private void handleIsntTracked(Message message, PrimaveraBot bot) {
        bot.respond(message.chat().id(), message.messageId(), ISNT_TRACKED);
    }
}
