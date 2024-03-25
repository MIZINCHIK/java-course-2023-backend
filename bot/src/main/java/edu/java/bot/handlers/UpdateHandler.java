package edu.java.bot.handlers;

import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.handlers.commands.Command;
import edu.java.model.storage.UserStorage;
import java.util.Map;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class UpdateHandler {
    @Getter
    private final Map<String, Command> commands;
    private final Command unknownCommand;
    private final UserStorage userStorage;

    public UpdateHandler(Map<String, Command> commands, Command unknownCommand, UserStorage userStorage) {
        this.commands = commands;
        this.unknownCommand = unknownCommand;
        this.userStorage = userStorage;
    }

    public void handleUpdate(Update update) {
        Message message = update.message();
        if (message == null) {
            if (update.myChatMember() != null
            && update.myChatMember().newChatMember().status().equals(ChatMember.Status.kicked)) {
                userStorage.deleteUser(update.myChatMember().chat().id());
            }
            return;
        }
        String[] args = message.text().trim().split(" ");
        try {
            commands.getOrDefault(args[0], unknownCommand).handle(message, args);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
