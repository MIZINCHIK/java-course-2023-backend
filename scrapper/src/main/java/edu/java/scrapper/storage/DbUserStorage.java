package edu.java.scrapper.storage;

import edu.java.model.storage.UserStorage;
import org.springframework.stereotype.Repository;

@Repository
public class DbUserStorage implements UserStorage {
    @Override
    public void registerUser(Long userId) {
        //TODO
    }

    @Override
    public boolean isUserRegistered(Long userId) {
        // TODO
        return true;
    }

    @Override
    public void deleteUser(Long userId) {
        //TODO
    }
}
