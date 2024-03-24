package edu.java.scrapper.service.jdbc;

import edu.java.model.storage.UserStorage;
import edu.java.scrapper.domain.JdbcUsersDao;
import edu.java.scrapper.exceptions.UserAlreadyRegisteredException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JdbcUserService implements UserStorage {
    private final JdbcUsersDao usersDao;

    @Override
    public void registerUser(long userId) throws UserAlreadyRegisteredException {
        try {
            usersDao.add(userId);
        } catch (DuplicateKeyException e) {
            throw new UserAlreadyRegisteredException(e);
        }
    }

    @Override
    public boolean isUserRegistered(long userId) {
        return usersDao.isUserRegistered(userId);
    }

    @Override
    public void deleteUser(long userId) {
        usersDao.remove(userId);
    }
}
