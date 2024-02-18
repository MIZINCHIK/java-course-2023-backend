package edu.java.bot.handlers.commands;

import com.pengrad.telegrambot.model.Message;
import edu.java.bot.PrimaveraBot;
import edu.java.bot.links.Link;
import static edu.java.bot.links.Link.isLinkCorrect;

public class TrackCommand extends Command {
    private static final String NAME = "track";
    private static final String DESCRIPTION = "Track a link. /track <link1> ...";

    public TrackCommand() {
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
                if (!isLinkCorrect(args[i])) {
                    handleLinkUnsupported(args[i], message, bot);
                } else {
                    bot.trackLink(new Link(args[i]), userId);
                    handleSuccess(message, bot);
                }
            }
        }
    }

    private void handleLinkUnsupported(String link, Message message, PrimaveraBot bot) {
        bot.respond(message.chat().id(), message.messageId(), "The following link is unsupported: " + link);
    }
}
