package edu.java.bot.handlers;

import edu.java.bot.PrimaveraBot;
import edu.java.bot.handlers.commands.Command;
import java.util.HashMap;
import java.util.Map;

public class UpdateHandlerBuilder {
    private final Map<String, Command> map;
    private final PrimaveraBot bot;

    public UpdateHandlerBuilder(PrimaveraBot bot) {
        this.map = new HashMap<>();
        this.bot = bot;
    }

    public UpdateHandlerBuilder addCommand(Command command) {
        map.put(command.getName(), command);
        return this;
    }

    public UpdateHandler build() {
        return new UpdateHandler(map, bot);
    }
}
