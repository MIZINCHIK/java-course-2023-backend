package edu.java.bot.handlers;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.PrimaveraBot;
import edu.java.bot.handlers.commands.Command;
import java.util.Map;
import lombok.Getter;

public class UpdateHandler {
    private static final String UNKNOWN_COMMAND = "Choose a command from the menu";
    @Getter
    private final Map<String, Command> commands;
    private final PrimaveraBot bot;

    public UpdateHandler(Map<String, Command> commands, PrimaveraBot bot) {
        this.commands = commands;
        this.bot = bot;
    }

    public void handleUpdate(Update update) {
        Message message = update.message();
        if (message == null) {
            return;
        }
        String[] args = message.text().trim().split(" ");
        if (commands.containsKey(args[0])) {
            commands.get(args[0]).handle(message, args, bot);
        } else {
            bot.respond(message.chat().id(), message.messageId(), UNKNOWN_COMMAND);
        }
    }
}
