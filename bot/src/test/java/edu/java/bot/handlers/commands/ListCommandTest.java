package edu.java.bot.handlers.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import edu.java.bot.PrimaveraBot;
import java.net.URI;
import java.util.List;
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
public class ListCommandTest {
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
    @DisplayName("User not registered")
    void handle_whenUserNotRegistered_thenSpecialMessage() {
        Mockito.when(bot.isUserRegistered(any())).thenReturn(false);
        Command list = new ListCommand();
        list.handle(message, new String[] {"/list"}, bot);
        verify(bot).respond(any(), any(), stringCaptor.capture());
        String value = stringCaptor.getValue();
        assertThat(value).isEqualTo("The user isn't registered. Use /start command.");
    }

    @Test
    @DisplayName("No saved links")
    void handle_whenNoSavedLinks_thenSpecialMessage() {
        Mockito.when(bot.isUserRegistered(any())).thenReturn(true);
        Mockito.when(bot.getLinksByUserId(any())).thenReturn(List.of());
        Command list = new ListCommand();
        list.handle(message, new String[] {"/list"}, bot);
        verify(bot).respond(any(), any(), stringCaptor.capture());
        String value = stringCaptor.getValue();
        assertThat(value).isEqualTo("No links being tracked.");
    }

    @Test
    @DisplayName("Several saved links")
    void handle_whenSeveralSavedLinks_thenCorrectTable() throws Exception {
        Mockito.when(bot.isUserRegistered(any())).thenReturn(true);
        Mockito.when(bot.getLinksByUserId(any())).thenReturn(List.of(
            new URI("http://123.com").toURL(),
            new URI("https://github.com").toURL(),
            new URI("https://stackoverflow.com").toURL(),
            new URI("https://github.com/232132131").toURL(),
            new URI("https://stackoverflow.com/21312/12321").toURL(),
            new URI("https://ya.ru").toURL()
        ));
        Command list = new ListCommand();
        list.handle(message, new String[] {"/list"}, bot);
        verify(bot).respondMd(any(), any(), stringCaptor.capture());
        String value = stringCaptor.getValue();
        assertThat(value.indexOf("GITHUB")).isGreaterThan(-1);
        assertThat(value.indexOf("https://github.com")).isGreaterThan(value.indexOf("GITHUB"));
        value = value.substring(value.indexOf("https://github.com"));
        assertThat(value.indexOf("SOF")).isGreaterThan(value.indexOf("https://github.com"));
        value = value.substring(value.indexOf("SOF"));
        assertThat(value.indexOf("https://stackoverflow.com")).isGreaterThan(value.indexOf("SOF"));
        value = value.substring(value.indexOf("https://stackoverflow.com"));
        assertThat(value.indexOf("GITHUB")).isGreaterThan(value.indexOf("https://stackoverflow.com"));
        value = value.substring(value.indexOf("GITHUB"));
        assertThat(value.indexOf("https://github.com/232132131")).isGreaterThan(value.indexOf("GITHUB"));
        value = value.substring(value.indexOf("https://github.com/232132131"));
        assertThat(value.indexOf("SOF ")).isGreaterThan(value.indexOf("https://github.com/232132131"));
        value = value.substring(value.indexOf("SOF"));
        assertThat(value.indexOf("https://stackoverflow.com/21312/12321")).isGreaterThan(value.indexOf("SOF"));
    }
}
