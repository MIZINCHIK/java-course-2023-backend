package edu.java.bot.handlers.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import edu.java.bot.PrimaveraBot;
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
public class UntrackCommandTest {
    @Mock
    private PrimaveraBot bot;
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
        Command untrack = new UntrackCommand();
        untrack.handle(message, new String[] {"/untrack"}, bot);
        verify(bot).respond(any(), any(), stringCaptor.capture());
        String value = stringCaptor.getValue();
        assertThat(value).isEqualTo("The arguments provided are incorrect. Use /help to learn more");
    }

    @Test
    @DisplayName("User not registered")
    void handle_whenUserNotRegistered_thenFailure() {
        Mockito.when(bot.isUserRegistered(any())).thenReturn(false);
        Command untrack = new UntrackCommand();
        untrack.handle(message, new String[] {"/untrack", "https://stackoverflow.com"}, bot);
        verify(bot).respond(any(), any(), stringCaptor.capture());
        String value = stringCaptor.getValue();
        assertThat(value).isEqualTo("The user isn't registered. Use /start command.");
    }

    @Test
    @DisplayName("Link not tracked")
    void handle_whenLinkIsntTracked_thenFailure() {
        Mockito.when(bot.isUserRegistered(any())).thenReturn(true);
        Mockito.when(bot.isLinkTracked(any())).thenReturn(false);
        Command untrack = new UntrackCommand();
        untrack.handle(message, new String[] {"/untrack", "https://stackoverflow.com"}, bot);
        verify(bot).respond(any(), any(), stringCaptor.capture());
        String value = stringCaptor.getValue();
        assertThat(value).isEqualTo("The link wasn't tracked already.");
    }

    @Test
    @DisplayName("Link is tracked")
    void handle_whenLinkIsTracked_thenSuccess() {
        Mockito.when(bot.isUserRegistered(any())).thenReturn(true);
        Mockito.when(bot.isLinkTracked(any())).thenReturn(true);
        Command untrack = new UntrackCommand();
        untrack.handle(message, new String[] {"/untrack", "https://stackoverflow.com"}, bot);
        verify(bot).respond(any(), any(), stringCaptor.capture());
        String value = stringCaptor.getValue();
        assertThat(value).isEqualTo("Success");
    }
}
