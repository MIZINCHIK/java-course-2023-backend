package edu.java.bot;

import edu.java.bot.handlers.BotExceptionHandler;
import edu.java.bot.handlers.UpdateHandler;
import edu.java.bot.handlers.UpdateHandlerBuilder;
import edu.java.bot.handlers.commands.HelpCommand;
import edu.java.bot.handlers.commands.ListCommand;
import edu.java.bot.handlers.commands.StartCommand;
import edu.java.bot.handlers.commands.TrackCommand;
import edu.java.bot.handlers.commands.UnknownCommand;
import edu.java.bot.handlers.commands.UntrackCommand;
import edu.java.model.storage.LinkStorage;
import edu.java.model.storage.UserStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BotRunner implements ApplicationRunner {
    private final PrimaveraBot bot;
    private final UserStorage userStorage;
    private final LinkStorage linkStorage;
    private UpdateHandler handler;

    @Override
    public void run(ApplicationArguments args) {
        configureHandler();
        bot.setCommands(handler.getCommands());
        bot.setUpdatesListener(new BotUpdatesListener(handler), new BotExceptionHandler());
    }

    private void configureHandler() {
        handler = new UpdateHandlerBuilder(new UnknownCommand(bot), userStorage)
            .addCommand(new StartCommand(bot, userStorage))
            .addCommand(new HelpCommand(bot))
            .addCommand(new TrackCommand(bot, userStorage, linkStorage))
            .addCommand(new UntrackCommand(bot, userStorage, linkStorage))
            .addCommand(new ListCommand(bot, linkStorage, userStorage))
            .build();
    }
}
