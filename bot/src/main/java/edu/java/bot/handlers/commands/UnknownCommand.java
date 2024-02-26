package edu.java.bot.handlers.commands;

import com.pengrad.telegrambot.model.Message;
import edu.java.bot.PrimaveraBot;

public class UnknownCommand extends Command {
    private static final String EMPTY = "404";
    private static final String UNKNOWN_COMMAND = "Choose a command from the menu";

    public UnknownCommand(PrimaveraBot bot) {
        super(EMPTY, EMPTY, bot);
    }

    @Override
    public void handle(Message message, String[] args) {
        bot.respond(message.chat().id(), message.messageId(), UNKNOWN_COMMAND);
    }
}
