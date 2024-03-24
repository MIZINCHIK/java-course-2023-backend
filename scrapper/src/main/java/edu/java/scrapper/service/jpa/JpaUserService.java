package edu.java.scrapper.service.jpa;

import edu.java.model.storage.UserStorage;
import edu.java.scrapper.domain.jpa.UserRepository;
import edu.java.scrapper.domain.jpa.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JpaUserService implements UserStorage {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void registerUser(long userId) {
        userRepository.save(new UserEntity(userId));
    }

    @Override
    @Transactional
    public boolean isUserRegistered(long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }
}
