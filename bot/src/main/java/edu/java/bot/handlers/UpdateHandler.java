package edu.java.bot.handlers;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.handlers.commands.Command;
import java.util.Map;
import lombok.Getter;

public class UpdateHandler {
    @Getter
    private final Map<String, Command> commands;
    private final Command unknownCommand;

    public UpdateHandler(Map<String, Command> commands, Command unknownCommand) {
        this.commands = commands;
        this.unknownCommand = unknownCommand;
    }

    public void handleUpdate(Update update) {
        Message message = update.message();
        if (message == null) {
            return;
        }
        String[] args = message.text().trim().split(" ");
        commands.getOrDefault(args[0], unknownCommand).handle(message, args);
    }
}
