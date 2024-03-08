package edu.java.scrapper.controller;

import edu.java.model.storage.UserStorage;
import edu.java.scrapper.exceptions.UserAlreadyRegisteredException;
import edu.java.scrapper.exceptions.UserNotRegisteredException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/tg-chat")
public class TgChatController implements TgChatApi {
    private final UserStorage userStorage;

    @GetMapping(value = "/{id}")
    @Override
    public ResponseEntity<Void> getChat(
        @PathVariable("id")
        Long id
    ) {
        if (userStorage.isUserRegistered(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping(value = "/{id}")
    @Override
    public ResponseEntity<Void> registerChat(
        @PathVariable("id")
        Long id
    ) {
        if (userStorage.isUserRegistered(id)) {
            throw new UserAlreadyRegisteredException();
        }
        userStorage.registerUser(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<Void> deleteChat(
        @PathVariable("id")
        Long id
    ) {
        if (!userStorage.isUserRegistered(id)) {
            throw new UserNotRegisteredException();
        }
        userStorage.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
