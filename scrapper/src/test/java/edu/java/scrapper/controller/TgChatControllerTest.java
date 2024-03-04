package edu.java.scrapper.controller;

import edu.java.model.storage.UserStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TgChatController.class)
public class TgChatControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserStorage userStorage;

    @Test
    public void getChat_whenIsntRegistered_then404() throws Exception {
        Mockito.when(userStorage.isUserRegistered(0L)).thenReturn(false);
        mvc.perform(get("/tg-chat/0")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
        verify(userStorage).isUserRegistered(0L);
        verifyNoMoreInteractions(userStorage);
    }

    @Test
    public void getChat_whenRegistered_then200() throws Exception {
        Mockito.when(userStorage.isUserRegistered(0L)).thenReturn(true);
        mvc.perform(get("/tg-chat/0")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(userStorage).isUserRegistered(0L);
        verifyNoMoreInteractions(userStorage);
    }

    @Test
    public void registerChat_whenIsntRegistered_then200() throws Exception {
        Mockito.when(userStorage.isUserRegistered(0L)).thenReturn(false);
        mvc.perform(post("/tg-chat/0")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(userStorage).isUserRegistered(0L);
        verify(userStorage).registerUser(0L);
        verifyNoMoreInteractions(userStorage);
    }

    @Test
    public void registerChat_whenRegistered_then409() throws Exception {
        Mockito.when(userStorage.isUserRegistered(0L)).thenReturn(true);
        mvc.perform(post("/tg-chat/0")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.description").value("Sought user is already registered"))
            .andExpect(jsonPath("$.code").value("409 CONFLICT"))
            .andExpect(jsonPath("$.exceptionName").value("edu.java.scrapper.exceptions.UserAlreadyRegisteredException"));
        verify(userStorage).isUserRegistered(0L);
        verifyNoMoreInteractions(userStorage);
    }

    @Test
    public void deleteChat_whenIsntRegistered_then404() throws Exception {
        Mockito.when(userStorage.isUserRegistered(0L)).thenReturn(false);
        mvc.perform(delete("/tg-chat/0")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.description").value("Sought user isn't registered"))
            .andExpect(jsonPath("$.code").value("404 NOT_FOUND"))
            .andExpect(jsonPath("$.exceptionName").value("edu.java.scrapper.exceptions.UserNotRegisteredException"));
        verify(userStorage).isUserRegistered(0L);
        verifyNoMoreInteractions(userStorage);
    }

    @Test
    public void deleteChat_whenRegistered_then200() throws Exception {
        Mockito.when(userStorage.isUserRegistered(0L)).thenReturn(true);
        mvc.perform(delete("/tg-chat/0")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(userStorage).isUserRegistered(0L);
        verify(userStorage).deleteUser(0L);
        verifyNoMoreInteractions(userStorage);
    }
}
