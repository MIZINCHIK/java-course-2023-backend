package edu.java.bot.handlers.commands;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
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
public class HelpCommandTest {
    @Mock
    private PrimaveraBot bot;
    @Mock
    private Message message;
    @Mock
    private Chat chat;
    @Captor
    ArgumentCaptor<String> stringCaptor;

    @BeforeEach
    void setUpMocks() {
        Mockito.when(bot.getCommands()).thenReturn(new BotCommand[] {
            new BotCommand("cmd1", "descr1"),
            new BotCommand("cmd2", "descr2"),
            new BotCommand("cmd3", "descr3")});
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(chat.id()).thenReturn(0L);
        Mockito.when(message.messageId()).thenReturn(0);
    }

    @Test
    @DisplayName("Various commands")
    void handle_whenSeveralCommands_thenAllInMessage() {
        Command help = new HelpCommand(bot);
        help.handle(message, new String[] {"/help"});
        verify(bot).respondMd(any(), any(), stringCaptor.capture());
        String value = stringCaptor.getValue();
        assertThat(value.indexOf("cmd1")).isGreaterThan(-1);
        assertThat(value.indexOf("descr1")).isGreaterThan(value.indexOf("cmd1"));
        assertThat(value.indexOf("cmd2")).isGreaterThan(value.indexOf("descr1"));
        assertThat(value.indexOf("descr2")).isGreaterThan(value.indexOf("cmd2"));
        assertThat(value.indexOf("cmd3")).isGreaterThan(value.indexOf("descr2"));
        assertThat(value.indexOf("descr3")).isGreaterThan(value.indexOf("cmd3"));
    }
}
