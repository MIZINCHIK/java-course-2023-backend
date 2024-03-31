package edu.java.bot.handlers.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import edu.java.bot.PrimaveraBot;
import edu.java.model.storage.UserStorage;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StartCommandTest {
    @Mock
    private PrimaveraBot bot;
    @Mock
    private UserStorage userStorage;
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
    @DisplayName("User not registered")
    void handle_whenUserNotRegistered_thenSuccess() {
        Mockito.when(userStorage.isUserRegistered(anyLong())).thenReturn(false);
        Command start = new StartCommand(bot, userStorage);
        start.handle(message, new String[] {"/start"});
        verify(bot).respond(any(), any(), stringCaptor.capture());
        String value = stringCaptor.getValue();
        assertThat(value).isEqualTo("Success");
    }

    @Test
    @DisplayName("User registered")
    void handle_whenRegistered_thenFailure() {
        Mockito.when(userStorage.isUserRegistered(anyLong())).thenReturn(true);
        Command start = new StartCommand(bot, userStorage);
        start.handle(message, new String[] {"/start"});
        verify(bot).respond(any(), any(), stringCaptor.capture());
        String value = stringCaptor.getValue();
        assertThat(value).isEqualTo("The user is already registered.");
    }
}
