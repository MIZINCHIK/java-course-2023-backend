package edu.java.bot;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.handlers.UpdateHandler;
import java.util.List;

public class BotUpdatesListener implements UpdatesListener {
    private final UpdateHandler handler;

    public BotUpdatesListener(UpdateHandler handler) {
        this.handler = handler;
    }

    @Override
    public int process(List<Update> updates) {
        for (var update : updates) {
            handler.handleUpdate(update);
        }
        return CONFIRMED_UPDATES_ALL;
    }
}
