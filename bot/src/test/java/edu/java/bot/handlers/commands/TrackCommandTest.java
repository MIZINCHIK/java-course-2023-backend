package edu.java.bot.handlers.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import edu.java.bot.PrimaveraBot;
import edu.java.bot.storage.LinkStorage;
import edu.java.bot.storage.UserStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TrackCommandTest {
    @Mock
    private PrimaveraBot bot;
    @Mock
    private UserStorage userStorage;
    @Mock
    private LinkStorage linkStorage;
    @Mock
    private Message message;
    @Mock
    private User user;
    @Mock
    private Chat chat;
    @Captor
    ArgumentCaptor<String> stringCaptor;

    @BeforeEach
    void setUpMocks() {
        Mockito.when(message.from()).thenReturn(user);
        Mockito.when(user.id()).thenReturn(0L);
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(chat.id()).thenReturn(0L);
        Mockito.when(message.messageId()).thenReturn(0);
    }

    @Test
    @DisplayName("Insufficient arguments")
    void handle_whenNotEnoughArguments_thenFailure() {
        Command track = new TrackCommand(bot, userStorage, linkStorage);
        track.handle(message, new String[] {"/track"});
        verify(bot).respond(any(), any(), stringCaptor.capture());
        String value = stringCaptor.getValue();
        assertThat(value).isEqualTo("The arguments provided are incorrect. Use /help to learn more");
    }

    @Test
    @DisplayName("User not registered")
    void handle_whenUserNotRegistered_thenFailure() {
        Mockito.when(userStorage.isUserRegistered(any())).thenReturn(false);
        Command track = new TrackCommand(bot, userStorage, linkStorage);
        track.handle(message, new String[] {"/track", "https://stackoverflow.com"});
        verify(bot).respond(any(), any(), stringCaptor.capture());
        String value = stringCaptor.getValue();
        assertThat(value).isEqualTo("The user isn't registered. Use /start command.");
    }

    @Test
    @DisplayName("Incorrect link")
    void handle_whenIncorrectLink_thenFailure() {
        Mockito.when(userStorage.isUserRegistered(any())).thenReturn(true);
        Command track = new TrackCommand(bot, userStorage, linkStorage);
        track.handle(message, new String[] {"/track", "sdfsdfdsf"});
        verify(bot).respond(any(), any(), stringCaptor.capture());
        String value = stringCaptor.getValue();
        assertThat(value).isEqualTo("The following link is unsupported: sdfsdfdsf");
    }

    @Test
    @DisplayName("Domain not supported")
    void handle_whenDomainNotSupported_thenFailure() {
        Mockito.when(userStorage.isUserRegistered(any())).thenReturn(true);
        Command track = new TrackCommand(bot, userStorage, linkStorage);
        track.handle(message, new String[] {"/track", "https://google.com"});
        verify(bot).respond(any(), any(), stringCaptor.capture());
        String value = stringCaptor.getValue();
        assertThat(value).isEqualTo("The following link is unsupported: https://google.com");
    }

    @Test
    @DisplayName("Domain not supported")
    void handle_whenStackoverflow_thenSuccess() {
        Mockito.when(userStorage.isUserRegistered(any())).thenReturn(true);
        Command track = new TrackCommand(bot, userStorage, linkStorage);
        track.handle(message, new String[] {"/track", "https://stackoverflow.com"});
        verify(bot).respond(any(), any(), stringCaptor.capture());
        String value = stringCaptor.getValue();
        assertThat(value).isEqualTo("Success");
    }
}
