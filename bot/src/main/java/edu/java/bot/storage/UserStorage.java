package edu.java.bot.storage;

public interface UserStorage {
    void registerUser(Long userId);

    boolean isUserRegistered(Long userId);
}
