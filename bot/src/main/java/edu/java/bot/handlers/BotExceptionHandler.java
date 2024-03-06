package edu.java.bot.handlers;

import com.pengrad.telegrambot.ExceptionHandler;
import com.pengrad.telegrambot.TelegramException;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class BotExceptionHandler implements ExceptionHandler {
    @Override
    public void onException(TelegramException e) {
        if (e.response() != null) {
            e.response().errorCode();
            e.response().description();
        } else {
            log.error(e.getStackTrace());
        }
    }
}
