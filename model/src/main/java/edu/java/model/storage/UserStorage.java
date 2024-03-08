package edu.java.model.storage;

public interface UserStorage {
    void registerUser(Long userId);

    boolean isUserRegistered(Long userId);

    void deleteUser(Long userId);
}
