package edu.java.bot.handlers;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.PrimaveraBot;
import edu.java.bot.handlers.commands.Command;
import java.util.List;
import java.util.Optional;
import lombok.Getter;

public class UpdateHandler {
    private static final String UNKNOWN_COMMAND = "Choose a command from the menu";
    @Getter
    private final List<Command> commands;
    private final PrimaveraBot bot;

    public UpdateHandler(List<Command> commands, PrimaveraBot bot) {
        this.commands = commands;
        this.bot = bot;
    }

    public void handleUpdate(Update update) {
        Message message = update.message();
        if (message == null) {
            return;
        }
        String[] args = message.text().trim().split(" ");
        Optional<Command> command = commands.stream()
            .filter(cmd -> cmd.getName().equals(args[0]))
            .findFirst();
        if (command.isEmpty()) {
            bot.respond(message.chat().id(), message.messageId(), UNKNOWN_COMMAND);
        } else {
            command.get().handle(message, args, bot);
        }
    }
}
