package edu.java.bot.handlers.commands;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Message;
import edu.java.bot.PrimaveraBot;
import lombok.Getter;

@Getter
public abstract class Command {
    public static final String WRONG_ARGUMENTS = "The arguments provided are incorrect. Use /help to learn more";
    public static final String USER_NOT_REGISTERED = "The user isn't registered. Use /start command.";
    public static final String SUCCESS = "Success";
    private final BotCommand command;

    public Command(String command, String description) {
        this.command = new BotCommand("/" + command, description);
    }

    public String getName() {
        return command.command();
    }

    public String getDescription() {
        return command.description();
    }

    public abstract void handle(Message message, String[] args, PrimaveraBot bot);

    public void handleIllegalArguments(Message message, PrimaveraBot bot) {
        bot.respond(message.chat().id(), message.messageId(), WRONG_ARGUMENTS);
    }

    public void handleUserNotRegistered(Message message, PrimaveraBot bot) {
        bot.respond(message.chat().id(), message.messageId(), USER_NOT_REGISTERED);
    }

    protected void handleSuccess(Message message, PrimaveraBot bot) {
        bot.respond(message.chat().id(), message.messageId(), SUCCESS);
    }
}
