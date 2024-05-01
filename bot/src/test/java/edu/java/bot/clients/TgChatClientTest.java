package edu.java.bot.clients;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import edu.java.backoff.BackoffType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@WireMockTest
public class TgChatClientTest {
    private static final String MAPPINGS = "src/test/resources/wiremock/tgchat";
    private TgChatClient client;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wmRuntimeInfo) {
        WireMock wireMock = wmRuntimeInfo.getWireMock();
        wireMock.loadMappingsFrom(MAPPINGS);
        client = new ClientsConfiguration().tgChatClient(
            WebClient.builder(),
            wmRuntimeInfo.getHttpBaseUrl(),
            BackoffType.LINEAR,
            Duration.of(1, ChronoUnit.SECONDS),
            5,
            List.of()
        );
    }

    @Test
    @DisplayName("GET 200")
    void getChat_when200_thenCorrect() {
        assertAll(() -> client.getChat(0L));
    }

    @Test
    @DisplayName("GET 400")
    void getChat_when400_thenException() {
        assertThatThrownBy(() -> client.getChat(1L))
            .isInstanceOf(HttpClientErrorException.class)
            .hasMessage("400 Tg chat client encountered and error");
    }

    @Test
    @DisplayName("POST 200")
    void postChat_when200_thenCorrect() {
        assertAll(() -> client.postChat(0L));
    }

    @Test
    @DisplayName("POST 400")
    void postChat_when400_thenException() {
        assertThatThrownBy(() -> client.postChat(1L))
            .isInstanceOf(HttpClientErrorException.class)
            .hasMessage("400 Tg chat client encountered and error");
    }

    @Test
    @DisplayName("DELETE 200")
    void deleteChat_when200_thenCorrect() {
        assertAll(() -> client.deleteChat(0L));
    }

    @Test
    @DisplayName("DELETE 400")
    void deleteChat_when400_thenException() {
        assertThatThrownBy(() -> client.deleteChat(1L))
            .isInstanceOf(HttpClientErrorException.class)
            .hasMessage("400 Tg chat client encountered and error");
    }
}
