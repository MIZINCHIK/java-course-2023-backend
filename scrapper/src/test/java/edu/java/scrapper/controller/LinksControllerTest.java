package edu.java.scrapper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.model.dto.LinkResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    }

    @Test
    public void getLinks_whenIncorrectArguments_then400() throws Exception {
        mvc.perform(get("/links")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Tg-Chat-Id", objectMapper.writeValueAsString(null)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.description").value("Malformed request: arguments mustn't be omitted"))
            .andExpect(jsonPath("$.code").value("400 BAD_REQUEST"))
            .andExpect(jsonPath("$.exceptionName").value("linkUpdate"));
    }
}
