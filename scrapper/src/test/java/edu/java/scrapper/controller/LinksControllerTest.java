package edu.java.scrapper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.model.dto.AddLinkRequest;
import edu.java.model.dto.LinkResponse;
import edu.java.model.dto.RemoveLinkRequest;
import edu.java.model.storage.UserStorage;
import edu.java.scrapper.service.ModifiableLinkStorage;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(LinksController.class)
public class LinksControllerTest {
    private static final Integer EXHAUST = 1001;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserStorage userStorage;
    @MockBean
    private ModifiableLinkStorage linkStorage;

    @Test
    @DisplayName("GET 200")
    public void getLinks_whenCorrectId_then200() throws Exception {
        Mockito.when(userStorage.isUserRegistered(0L)).thenReturn(true);
        List<LinkResponse> links = List.of(new LinkResponse(0L, new URI("https://www.github.com")));
        Mockito.when(linkStorage.getLinksByUserId(0L))
            .thenReturn(links);
        mvc.perform(get("/links")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Tg-Chat-Id", objectMapper.writeValueAsString(0L)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.links[0].id").value(links.getFirst().id()))
            .andExpect(jsonPath("$.links[0].url").value(links.getFirst().url().toString()))
            .andExpect(jsonPath("$.size").value(links.size()));
        verify(userStorage).isUserRegistered(0L);
        verify(linkStorage).getLinksByUserId(0L);
        verifyNoMoreInteractions(userStorage);
        verifyNoMoreInteractions(linkStorage);
    }

    @Test
    @DisplayName("GET 400 missing arguments")
    public void getLinks_whenMissingArguments_then400() throws Exception {
        mvc.perform(get("/links")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.description").value(
                "Required request header 'Tg-Chat-Id' for method parameter type Long is not present"))
            .andExpect(jsonPath("$.code").value("400 BAD_REQUEST"))
            .andExpect(jsonPath("$.exceptionName").value("org.springframework.web.bind.MissingRequestHeaderException"));
        verifyNoInteractions(userStorage);
        verifyNoInteractions(linkStorage);
    }

    @Test
    @DisplayName("GET 400 wrong arguments")
    public void getLinks_whenIncorrectArguments_then400() throws Exception {
        mvc.perform(get("/links")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Tg-Chat-Id", objectMapper.writeValueAsString(null)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.description").value(
                "Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; For input string: \"null\""))
            .andExpect(jsonPath("$.code").value("400 BAD_REQUEST"))
            .andExpect(jsonPath("$.exceptionName").value(
                "org.springframework.web.method.annotation.MethodArgumentTypeMismatchException"));
        verifyNoInteractions(userStorage);
        verifyNoInteractions(linkStorage);
    }

    @Test
    @DisplayName("POST 200")
    public void postLink_whenCorrectId_then200() throws Exception {
        Mockito.when(userStorage.isUserRegistered(0L)).thenReturn(true);
        AddLinkRequest addLinkRequest = new AddLinkRequest(new URI("https://www.github.com"));
        Mockito.when(linkStorage.trackLink(any(), eq(0L))).thenReturn(0L);
        mvc.perform(post("/links")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Tg-Chat-Id", objectMapper.writeValueAsString(0L))
                .content(objectMapper.writeValueAsString(addLinkRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(0))
            .andExpect(jsonPath("$.url").value("https://www.github.com"));
        verify(userStorage).isUserRegistered(0L);
        verify(linkStorage).trackLink(any(), eq(0L));
        verifyNoMoreInteractions(userStorage);
        verifyNoMoreInteractions(linkStorage);
    }

    @Test
    @DisplayName("POST 400 missing header")
    public void postLink_whenMissingArguments_then400() throws Exception {
        Mockito.when(userStorage.isUserRegistered(0L)).thenReturn(true);
        AddLinkRequest addLinkRequest = new AddLinkRequest(new URI("https://www.github.com"));
        Mockito.when(linkStorage.trackLink(any(), eq(0L))).thenReturn(0L);
        mvc.perform(post("/links")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addLinkRequest)))
            .andExpect(jsonPath("$.description").value(
                "Required request header 'Tg-Chat-Id' for method parameter type Long is not present"))
            .andExpect(jsonPath("$.code").value("400 BAD_REQUEST"))
            .andExpect(jsonPath("$.exceptionName").value("org.springframework.web.bind.MissingRequestHeaderException"));
        verifyNoInteractions(userStorage);
        verifyNoInteractions(linkStorage);
    }

    @Test
    @DisplayName("POST 400 incorrect URL")
    public void postLink_whenIncorrectArguments_then400() throws Exception {
        Mockito.when(userStorage.isUserRegistered(0L)).thenReturn(true);
        AddLinkRequest addLinkRequest = new AddLinkRequest(new URI("https://www.google.com"));
        mvc.perform(post("/links")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Tg-Chat-Id", objectMapper.writeValueAsString(0L))
                .content(objectMapper.writeValueAsString(addLinkRequest)))
            .andExpect(jsonPath("$.description").value("URL provided isn't supported"))
            .andExpect(jsonPath("$.code").value("400 BAD_REQUEST"))
            .andExpect(jsonPath("$.exceptionName").value("edu.java.model.exceptions.MalformedUrlException"));
        verify(userStorage).isUserRegistered(0L);
        verifyNoMoreInteractions(userStorage);
        verifyNoInteractions(linkStorage);
    }

    @Test
    @DisplayName("POST 404")
    public void postLink_whenUserIsntRegistered_then404() throws Exception {
        Mockito.when(userStorage.isUserRegistered(0L)).thenReturn(false);
        AddLinkRequest addLinkRequest = new AddLinkRequest(new URI("https://www.github.com"));
        mvc.perform(post("/links")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Tg-Chat-Id", objectMapper.writeValueAsString(0L))
                .content(objectMapper.writeValueAsString(addLinkRequest)))
            .andExpect(jsonPath("$.description").value("Sought user isn't registered"))
            .andExpect(jsonPath("$.code").value("404 NOT_FOUND"))
            .andExpect(jsonPath("$.exceptionName").value("edu.java.scrapper.exceptions.UserNotRegisteredException"));
        verify(userStorage).isUserRegistered(0L);
        verifyNoMoreInteractions(userStorage);
        verifyNoInteractions(linkStorage);
    }

    @Test
    @DisplayName("DELETE 200")
    public void deleteLink_whenCorrectId_then200() throws Exception {
        Mockito.when(userStorage.isUserRegistered(0L)).thenReturn(true);
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(new URI("https://www.github.com"));
        Mockito.when(linkStorage.untrackLink(any(), eq(0L))).thenReturn(0L);
        Mockito.when(linkStorage.isLinkTracked(any(), eq(0L))).thenReturn(true);
        mvc.perform(delete("/links")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Tg-Chat-Id", objectMapper.writeValueAsString(0L))
                .content(objectMapper.writeValueAsString(removeLinkRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(0))
            .andExpect(jsonPath("$.url").value("https://www.github.com"));
        verify(userStorage).isUserRegistered(0L);
        verify(linkStorage).isLinkTracked(any(), eq(0L));
        verify(linkStorage).untrackLink(any(), eq(0L));
        verifyNoMoreInteractions(userStorage);
        verifyNoMoreInteractions(linkStorage);
    }

    @Test
    @DisplayName("DELETE 400 missing header")
    public void deleteLink_whenMissingArguments_then400() throws Exception {
        Mockito.when(userStorage.isUserRegistered(0L)).thenReturn(true);
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(new URI("https://www.github.com"));
        Mockito.when(linkStorage.trackLink(any(), eq(0L))).thenReturn(0L);
        mvc.perform(delete("/links")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(removeLinkRequest)))
            .andExpect(jsonPath("$.description").value(
                "Required request header 'Tg-Chat-Id' for method parameter type Long is not present"))
            .andExpect(jsonPath("$.code").value("400 BAD_REQUEST"))
            .andExpect(jsonPath("$.exceptionName").value("org.springframework.web.bind.MissingRequestHeaderException"));
        verifyNoInteractions(userStorage);
        verifyNoInteractions(linkStorage);
    }

    @Test
    @DisplayName("DELETE 400 incorrect URL")
    public void deleteLink_whenIncorrectArguments_then400() throws Exception {
        Mockito.when(userStorage.isUserRegistered(0L)).thenReturn(true);
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(new URI("https://www.google.com"));
        mvc.perform(delete("/links")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Tg-Chat-Id", objectMapper.writeValueAsString(0L))
                .content(objectMapper.writeValueAsString(removeLinkRequest)))
            .andExpect(jsonPath("$.description").value("URL provided isn't supported"))
            .andExpect(jsonPath("$.code").value("400 BAD_REQUEST"))
            .andExpect(jsonPath("$.exceptionName").value("edu.java.model.exceptions.MalformedUrlException"));
        verify(userStorage).isUserRegistered(0L);
        verifyNoMoreInteractions(userStorage);
        verifyNoInteractions(linkStorage);
    }

    @Test
    @DisplayName("DELETE 404")
    public void deleteLink_whenUserIsntRegistered_then404() throws Exception {
        Mockito.when(userStorage.isUserRegistered(0L)).thenReturn(false);
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(new URI("https://www.github.com"));
        mvc.perform(delete("/links")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Tg-Chat-Id", objectMapper.writeValueAsString(0L))
                .content(objectMapper.writeValueAsString(removeLinkRequest)))
            .andExpect(jsonPath("$.description").value("Sought user isn't registered"))
            .andExpect(jsonPath("$.code").value("404 NOT_FOUND"))
            .andExpect(jsonPath("$.exceptionName").value("edu.java.scrapper.exceptions.UserNotRegisteredException"));
        verify(userStorage).isUserRegistered(0L);
        verifyNoMoreInteractions(userStorage);
        verifyNoInteractions(linkStorage);
    }
}
