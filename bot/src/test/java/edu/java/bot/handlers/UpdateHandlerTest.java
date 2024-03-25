package edu.java.bot.handlers;

import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.model.ChatMemberUpdated;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.PrimaveraBot;
import edu.java.bot.handlers.commands.Command;
import edu.java.model.storage.UserStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest {
    @Mock
    Update update;
    @Mock
    private Message message;
    @Mock
    Command cmd1;
    @Mock
    Command cmd2;
    @Mock
    Command cmd3;
    @Mock
    Command cmd4;
    @Mock
    Command unknownCommand;
    @Mock
    UserStorage userStorage;

    @Test
    @DisplayName("No relevant command")
    void handleUpdate_whenNoCommandWithGivenName_thenSpecialMessage() {
        Mockito.when(update.message()).thenReturn(message);
        UpdateHandler handler = new UpdateHandlerBuilder(unknownCommand, userStorage)
            .addCommand(cmd1).addCommand(cmd2).addCommand(cmd3).addCommand(cmd4).build();
        Mockito.when(message.text()).thenReturn("/aaa");
        handler.handleUpdate(update);
        verify(unknownCommand).handle(eq(message), eq(new String[] {"/aaa"}));
    }

    @Test
    @DisplayName("Command is in the list")
    void handleUpdate_whenCommandIsPresent_thenHandle() {
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(cmd1.getName()).thenReturn("/cmd1");
        Mockito.when(cmd2.getName()).thenReturn("/cmd2");
        UpdateHandler handler = new UpdateHandlerBuilder(unknownCommand, userStorage)
            .addCommand(cmd1).addCommand(cmd2).addCommand(cmd3).addCommand(cmd4).build();
        Mockito.when(message.text()).thenReturn("/cmd2");
        handler.handleUpdate(update);
        verify(cmd2).handle(any(), eq(new String[] {"/cmd2"}));
    }

    @Test
    @DisplayName("User kicked")
    void handleUpdate_whenUserKicked_thenUserDeleted() {
        ChatMemberUpdated myChatMember = mock(ChatMemberUpdated.class, RETURNS_DEEP_STUBS);
        when(update.message()).thenReturn(null);
        when(update.myChatMember()).thenReturn(myChatMember);
        when(myChatMember.newChatMember().status()).thenReturn(ChatMember.Status.kicked);
        when(myChatMember.chat().id()).thenReturn(1L);
        UpdateHandler handler = new UpdateHandlerBuilder(unknownCommand, userStorage).build();
        handler.handleUpdate(update);
        verify(userStorage).deleteUser(1L);
    }

    @Test
    @DisplayName("User not kicked but message is null")
    void handleUpdate_whenMessageNull_thenUserDeleted() {
        ChatMemberUpdated myChatMember = mock(ChatMemberUpdated.class, RETURNS_DEEP_STUBS);
        when(update.message()).thenReturn(null);
        when(update.myChatMember()).thenReturn(null);
        UpdateHandler handler = new UpdateHandlerBuilder(unknownCommand, userStorage).build();
        handler.handleUpdate(update);
        verifyNoMoreInteractions(userStorage);
    }
}
