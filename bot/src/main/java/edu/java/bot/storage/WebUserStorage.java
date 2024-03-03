package edu.java.bot.storage;

import edu.java.bot.clients.TgChatClient;
import edu.java.model.storage.UserStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;

@Repository
@RequiredArgsConstructor
public class WebUserStorage implements UserStorage {
    private final TgChatClient tgChatClient;

    @Override
    public void registerUser(Long userId) throws HttpClientErrorException {
        tgChatClient.postChat(userId);
    }

    @Override
    public boolean isUserRegistered(Long userId) {
        try {
            tgChatClient.getChat(userId);
        } catch (HttpClientErrorException ignored) {
            return false;
        }
        return true;
    }

    @Override
    public void deleteUser(Long userId) throws HttpClientErrorException {
        tgChatClient.deleteChat(userId);
    }
}
