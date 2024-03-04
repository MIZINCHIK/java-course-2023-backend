package edu.java.scrapper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.model.dto.AddLinkRequest;
import edu.java.model.dto.LinkResponse;
import edu.java.model.links.Link;
import edu.java.model.storage.LinkStorage;
import edu.java.model.storage.UserStorage;
import java.net.URI;
import java.util.List;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(LinksController.class)
public class LinksControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserStorage userStorage;
    @MockBean
    private LinkStorage linkStorage;

    @Test
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
    public void getLinks_whenMissingArguments_then400() throws Exception {
        mvc.perform(get("/links")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.description").value("Required request header 'Tg-Chat-Id' for method parameter type Long is not present"))
            .andExpect(jsonPath("$.code").value("400 BAD_REQUEST"))
            .andExpect(jsonPath("$.exceptionName").value("org.springframework.web.bind.MissingRequestHeaderException"));
        verifyNoInteractions(userStorage);
        verifyNoInteractions(linkStorage);
    }

    @Test
    public void getLinks_whenIncorrectArguments_then400() throws Exception {
        mvc.perform(get("/links")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Tg-Chat-Id", objectMapper.writeValueAsString(null)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.description").value("Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; For input string: \"null\""))
            .andExpect(jsonPath("$.code").value("400 BAD_REQUEST"))
            .andExpect(jsonPath("$.exceptionName").value("org.springframework.web.method.annotation.MethodArgumentTypeMismatchException"));
        verifyNoInteractions(userStorage);
        verifyNoInteractions(linkStorage);
    }

    @Test
    public void postLink_whenCorrectId_then200() throws Exception {
        Mockito.when(userStorage.isUserRegistered(0L)).thenReturn(true);
        AddLinkRequest addLinkRequest = new AddLinkRequest(new URI("https://www.github.com"));
        Link link = new Link(addLinkRequest.link().toString());
        Mockito.when(linkStorage.trackLink(link, 0L)).thenReturn(0L);
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
    public void postLink_whenMissingArguments_then400() throws Exception {
        Mockito.when(userStorage.isUserRegistered(0L)).thenReturn(true);
        AddLinkRequest addLinkRequest = new AddLinkRequest(new URI("https://www.github.com"));
        Mockito.when(linkStorage.trackLink(new Link(addLinkRequest.link().toString()), 0L)).thenReturn(0L);
        mvc.perform(post("/links")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addLinkRequest)))
            .andExpect(jsonPath("$.description").value("Required request header 'Tg-Chat-Id' for method parameter type Long is not present"))
            .andExpect(jsonPath("$.code").value("400 BAD_REQUEST"))
            .andExpect(jsonPath("$.exceptionName").value("org.springframework.web.bind.MissingRequestHeaderException"));
        verifyNoInteractions(userStorage);
        verifyNoInteractions(linkStorage);
    }

    @Test
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
}
