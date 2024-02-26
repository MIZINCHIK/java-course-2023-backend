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
import java.util.Map;

public class PrimaveraBot {
    private final TelegramBot bot;

    public PrimaveraBot(String token) {
        bot = new TelegramBot(token);
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
}
