package edu.java.bot;

import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.handlers.BotExceptionHandler;
import edu.java.bot.handlers.UpdateHandler;
import edu.java.bot.handlers.commands.HelpCommand;
import edu.java.bot.handlers.commands.ListCommand;
import edu.java.bot.handlers.commands.StartCommand;
import edu.java.bot.handlers.commands.TrackCommand;
import edu.java.bot.handlers.commands.UntrackCommand;
import edu.java.bot.storage.DbStorage;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class BotRunner implements ApplicationRunner {
    private final String token;
    private PrimaveraBot bot;
    private UpdateHandler handler;

    @Autowired
    public BotRunner(ApplicationConfig applicationConfig) {
        token = applicationConfig.telegramToken();
    }

    @Override
    public void run(ApplicationArguments args) {
        bot = new PrimaveraBot(token, new DbStorage());
        configureHandler();
        bot.setCommands(handler.getCommands());
        bot.setUpdatesListener(new BotUpdatesListener(handler), new BotExceptionHandler());
    }

    private void configureHandler() {
        handler = new UpdateHandler(
            List.of(
                new StartCommand(),
                new HelpCommand(),
                new TrackCommand(),
                new UntrackCommand(),
                new ListCommand()
            ),
            bot
        );
    }
}
