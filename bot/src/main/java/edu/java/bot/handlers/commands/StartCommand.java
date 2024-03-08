package edu.java.bot.handlers.commands;

import com.pengrad.telegrambot.model.Message;
import edu.java.bot.PrimaveraBot;
import edu.java.model.storage.UserStorage;

public class StartCommand extends Command {
    public static final String NAME = "start";
    private static final String DESCRIPTION = "Register user. No arguments.";
    private static final String ALREADY_REGISTERED = "The user is already registered.";
    private final UserStorage userStorage;

    public StartCommand(PrimaveraBot bot, UserStorage userStorage) {
        super(NAME, DESCRIPTION, bot);
        this.userStorage = userStorage;
    }

    @Override
    public void handle(Message message, String[] args) {
        Long userId = message.from().id();
        if (userStorage.isUserRegistered(userId)) {
            handleAlreadyRegistered(message);
        } else {
            userStorage.registerUser(userId);
            handleSuccess(message);
        }
    }

    private void handleAlreadyRegistered(Message message) {
        bot.respond(message.chat().id(), message.messageId(), ALREADY_REGISTERED);
    }
}
