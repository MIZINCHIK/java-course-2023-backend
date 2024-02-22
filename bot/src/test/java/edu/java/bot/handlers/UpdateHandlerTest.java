package edu.java.bot.handlers;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.PrimaveraBot;
import edu.java.bot.handlers.commands.Command;
import java.util.Map;
import java.util.stream.Collectors;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest {
    @Mock
    private PrimaveraBot bot;
    @Mock
    Update update;
    @Mock
    private Message message;
    @Mock
    private Chat chat;
    @Captor
    ArgumentCaptor<String> stringCaptor;
    @Mock
    Command cmd1;
    @Mock
    Command cmd2;
    @Mock
    Command cmd3;
    @Mock
    Command cmd4;

    @Test
    @DisplayName("No relevant command")
    void handleUpdate_whenNoCommandWithGivenName_thenSpecialMessage() {
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(chat.id()).thenReturn(0L);
        Mockito.when(message.messageId()).thenReturn(0);
        Mockito.when(update.message()).thenReturn(message);
        UpdateHandler handler = new UpdateHandlerBuilder(bot)
            .addCommand(cmd1).addCommand(cmd2).addCommand(cmd3).addCommand(cmd4).build();
        Mockito.when(message.text()).thenReturn("/aaa");
        handler.handleUpdate(update);
        verify(bot).respond(any(), any(), stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo("Choose a command from the menu");
    }

    @Test
    @DisplayName("Command is in the list")
    void handleUpdate_whenCommandIsPresent_thenHandle() {
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(cmd1.getName()).thenReturn("/cmd1");
        Mockito.when(cmd2.getName()).thenReturn("/cmd2");
        UpdateHandler handler = new UpdateHandlerBuilder(bot)
            .addCommand(cmd1).addCommand(cmd2).addCommand(cmd3).addCommand(cmd4).build();
        Mockito.when(message.text()).thenReturn("/cmd2");
        handler.handleUpdate(update);
        verify(cmd2).handle(any(), eq(new String[] {"/cmd2"}), eq(bot));
    }
}
