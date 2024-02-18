package edu.java.bot.handlers.commands;

import com.pengrad.telegrambot.model.Message;
import edu.java.bot.PrimaveraBot;

public class StartCommand extends Command {
    private static final String NAME = "start";
    private static final String DESCRIPTION = "Register user. No arguments.";
    private static final String ALREADY_REGISTERED = "The user is already registered.";

    public StartCommand() {
        super(NAME, DESCRIPTION);
    }

    @Override
    public void handle(Message message, String[] args, PrimaveraBot bot) {
        Long userId = message.from().id();
        if (bot.isUserRegistered(userId)) {
            handleAlreadyRegistered(message, bot);
        } else {
            bot.registerUser(userId);
            handleSuccess(message, bot);
        }
    }

    private void handleAlreadyRegistered(Message message, PrimaveraBot bot) {
        bot.respond(message.chat().id(), message.messageId(), ALREADY_REGISTERED);
    }
}
