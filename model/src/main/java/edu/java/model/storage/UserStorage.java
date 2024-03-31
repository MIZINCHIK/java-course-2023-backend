package edu.java.model.storage;

public interface UserStorage {
    void registerUser(long userId);

    boolean isUserRegistered(long userId);

    void deleteUser(long userId);
}
