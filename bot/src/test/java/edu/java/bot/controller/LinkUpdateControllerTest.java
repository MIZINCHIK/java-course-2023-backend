package edu.java.bot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.bot.service.LinkUpdateService;
import edu.java.model.dto.LinkUpdate;
import java.net.URI;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(LinkUpdateController.class)
public class LinkUpdateControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private LinkUpdateService service;

    @Test
    @DisplayName("POST 200")
    public void sendUpdate_whenCorrectUpdate_then200() throws Exception {
        LinkUpdate update =
            new LinkUpdate(0L, new URI("https://www.github.com"), "sdad", List.of());
        mvc.perform(post("/updates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
            .andExpect(status().isOk());
        verify(service).processUpdate(eq(update));
    }

    @ParameterizedTest
    @MethodSource("generateIncorrectUpdates")
    @DisplayName("POST 400")
    public void sendUpdate_whenIncorrectUpdate_then400(Long id, String description, List<Long> tgChatIds)
        throws Exception {
        LinkUpdate update =
            new LinkUpdate(id, new URI("https://www.github.com"), description, tgChatIds);
        mvc.perform(post("/updates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.description").value("Malformed request: arguments mustn't be omitted"))
            .andExpect(jsonPath("$.code").value("400 BAD_REQUEST"))
            .andExpect(jsonPath("$.exceptionName").value("linkUpdate"));
        verify(service, never()).processUpdate(eq(update));
    }

    static Stream<Arguments> generateIncorrectUpdates() {
        return Stream.of(
            Arguments.of(null, "sadadsad", List.of()),
            Arguments.of(1L, null, List.of()),
            Arguments.of(1L, "asdasdsad", null)
        );
    }
}
