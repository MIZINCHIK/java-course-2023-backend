package edu.java.bot.handlers;

import edu.java.bot.handlers.commands.Command;
import java.util.HashMap;
import java.util.Map;

public class UpdateHandlerBuilder {
    private final Map<String, Command> map;
    private final Command unknownCommand;

    public UpdateHandlerBuilder(Command unknownCommand) {
        this.map = new HashMap<>();
        this.unknownCommand = unknownCommand;
    }

    public UpdateHandlerBuilder addCommand(Command command) {
        map.put(command.getName(), command);
        return this;
    }

    public UpdateHandler build() {
        return new UpdateHandler(map, unknownCommand);
    }
}
