package edu.java.bot;

import com.pengrad.telegrambot.ExceptionHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.GetMyCommands;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.handlers.commands.Command;
import edu.java.bot.links.Link;
import edu.java.bot.storage.Storage;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class PrimaveraBot implements Storage {
    private final TelegramBot bot;
    private final Storage storage;

    public PrimaveraBot(String token, Storage storage) {
        bot = new TelegramBot(token);
        this.storage = storage;
    }

    public void setCommands(Map<String, Command> commands) {
        bot.execute(new SetMyCommands(commands.values().stream()
            .map(Command::getCommand)
            .toArray(BotCommand[]::new)));
    }

    public BotCommand[] getCommands() {
        return bot.execute(new GetMyCommands()).commands();
    }

    public void setUpdatesListener(UpdatesListener listener, ExceptionHandler exceptionHandler) {
        bot.setUpdatesListener(listener, exceptionHandler);
    }

    public void respond(Long chatId, Integer messageId, String response) {
        bot.execute(new SendMessage(chatId, response)
            .replyToMessageId(messageId));
    }

    public void respondMd(Long chatId, Integer messageId, String response) {
        bot.execute(new SendMessage(chatId, response).parseMode(ParseMode.MarkdownV2)
            .replyToMessageId(messageId));
    }

    @Override
    public List<URL> getLinksByUserId(Long userId) {
        return storage.getLinksByUserId(userId);
    }

    @Override
    public void trackLink(Link link, Long userId) {
        storage.trackLink(link, userId);
    }

    @Override
    public void untrackLink(Link link, Long userId) {
        storage.untrackLink(link, userId);
    }

    @Override
    public void registerUser(Long userId) {
        storage.registerUser(userId);
    }

    @Override
    public boolean isUserRegistered(Long userId) {
        return storage.isUserRegistered(userId);
    }

    @Override
    public boolean isLinkTracked(Link link) {
        return storage.isLinkTracked(link);
    }
}
