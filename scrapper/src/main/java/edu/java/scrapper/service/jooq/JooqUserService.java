package edu.java.scrapper.service.jooq;

import edu.java.model.storage.UserStorage;
import edu.java.scrapper.exceptions.UserAlreadyRegisteredException;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import static edu.java.scrapper.domain.jooq.Tables.USERS;

@Service
@RequiredArgsConstructor
public class JooqUserService implements UserStorage {
    private final DSLContext dslContext;

    @Override
    public void registerUser(long userId) {
        try {
            dslContext.insertInto(USERS, USERS.ID).values(userId).execute();
        } catch (DuplicateKeyException e) {
            throw new UserAlreadyRegisteredException(e);
        }
    }

    @Override
    public boolean isUserRegistered(long userId) {
        return dslContext.fetchExists(dslContext.selectOne().from(USERS).where(USERS.ID.eq(userId)));
    }

    @Override
    public void deleteUser(long userId) {
        dslContext.deleteFrom(USERS).where(USERS.ID.eq(userId)).execute();
    }
}
