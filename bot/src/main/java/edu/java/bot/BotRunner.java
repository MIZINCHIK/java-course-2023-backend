package edu.java.bot;

import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.handlers.BotExceptionHandler;
import edu.java.bot.handlers.UpdateHandler;
import edu.java.bot.handlers.UpdateHandlerBuilder;
import edu.java.bot.handlers.commands.HelpCommand;
import edu.java.bot.handlers.commands.ListCommand;
import edu.java.bot.handlers.commands.StartCommand;
import edu.java.bot.handlers.commands.TrackCommand;
import edu.java.bot.handlers.commands.UnknownCommand;
import edu.java.bot.handlers.commands.UntrackCommand;
import edu.java.bot.storage.DbLinkStorage;
import edu.java.bot.storage.DbUserStorage;
import edu.java.bot.storage.LinkStorage;
import edu.java.bot.storage.UserStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class BotRunner implements ApplicationRunner {
    private final String token;
    private PrimaveraBot bot;
    private UserStorage userStorage;
    private LinkStorage linkStorage;
    private UpdateHandler handler;

    @Autowired
    public BotRunner(ApplicationConfig applicationConfig) {
        token = applicationConfig.telegramToken();
    }

    @Override
    public void run(ApplicationArguments args) {
        bot = new PrimaveraBot(token);
        userStorage = new DbUserStorage();
        linkStorage = new DbLinkStorage();
        configureHandler();
        bot.setCommands(handler.getCommands());
        bot.setUpdatesListener(new BotUpdatesListener(handler), new BotExceptionHandler());
    }

    private void configureHandler() {
        handler = new UpdateHandlerBuilder(new UnknownCommand(bot))
            .addCommand(new StartCommand(bot, userStorage))
            .addCommand(new HelpCommand(bot))
            .addCommand(new TrackCommand(bot, userStorage, linkStorage))
            .addCommand(new UntrackCommand(bot, userStorage, linkStorage))
            .addCommand(new ListCommand(bot, linkStorage, userStorage))
            .build();
    }
}
