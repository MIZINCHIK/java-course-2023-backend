package edu.java.bot.handlers;

import edu.java.bot.handlers.commands.Command;
import edu.java.model.storage.UserStorage;
import java.util.HashMap;
import java.util.Map;

public class UpdateHandlerBuilder {
    private final Map<String, Command> map;
    private final Command unknownCommand;
    private final UserStorage userStorage;

    public UpdateHandlerBuilder(Command unknownCommand, UserStorage userStorage) {
        this.map = new HashMap<>();
        this.unknownCommand = unknownCommand;
        this.userStorage = userStorage;
    }

    public UpdateHandlerBuilder addCommand(Command command) {
        map.put(command.getName(), command);
        return this;
    }

    public UpdateHandler build() {
        return new UpdateHandler(map, unknownCommand, userStorage);
    }
}
