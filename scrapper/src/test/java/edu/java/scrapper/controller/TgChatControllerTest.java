package edu.java.scrapper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(LinksController.class)
public class TgChatControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserStorage userStorage;

    @Test
    public void getChat_whenIsntRegistered_then404() throws Exception {
        Mockito.when(userStorage.isUserRegistered(0L)).thenReturn(false);
        mvc.perform(get("/0")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.description").value("Required request header 'Tg-Chat-Id' for method parameter type Long is not present"))
            .andExpect(jsonPath("$.code").value("400 BAD_REQUEST"))
            .andExpect(jsonPath("$.exceptionName").value("org.springframework.web.bind.MissingRequestHeaderException"));
    }

//    @Test
//    public void getLinks_whenMissingArguments_then400() throws Exception {
//        mvc.perform(get("/links")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isBadRequest())
//            .andExpect(jsonPath("$.description").value("Required request header 'Tg-Chat-Id' for method parameter type Long is not present"))
//            .andExpect(jsonPath("$.code").value("400 BAD_REQUEST"))
//            .andExpect(jsonPath("$.exceptionName").value("org.springframework.web.bind.MissingRequestHeaderException"));
//    }
}
