package edu.java.scrapper.service.jpa;

import edu.java.model.storage.UserStorage;
import edu.java.scrapper.domain.jpa.UserRepository;
import edu.java.scrapper.domain.jpa.entities.UserEntity;
import edu.java.scrapper.exceptions.UserAlreadyRegisteredException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JpaUserService implements UserStorage {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void registerUser(long userId) {
        if (userRepository.findById(userId).isPresent()) {
            throw new UserAlreadyRegisteredException();
        }
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
