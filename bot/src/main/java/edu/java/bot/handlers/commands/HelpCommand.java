package edu.java.bot.handlers.commands;

import com.pengrad.telegrambot.model.Message;
import edu.java.bot.PrimaveraBot;
import static edu.java.bot.handlers.MessageFormatter.buildCommands;

public class HelpCommand extends Command {
    public static final String NAME = "help";
    private static final String DESCRIPTION = "Show commands. No arguments.";

    public HelpCommand(PrimaveraBot bot) {
        super(NAME, DESCRIPTION, bot);
    }

    public void handle(Message message, String[] args) {
        bot.respondMd(message.chat().id(), message.messageId(), buildCommands(bot.getCommands()));
    }
}
