package edu.java.scrapper.clients;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.model.dto.LinkUpdate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import java.net.URI;
import java.util.List;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@WireMockTest
public class BotClientTest {
    private static final String MAPPINGS = "src/test/resources/wiremock/bot";
    private BotClient client;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wmRuntimeInfo) {
        WireMock wireMock = wmRuntimeInfo.getWireMock();
        wireMock.loadMappingsFrom(MAPPINGS);
        client = new ClientsConfiguration().botClient(
            WebClient.builder(),
            wmRuntimeInfo.getHttpBaseUrl()
        );
    }

    @Test
    @DisplayName("200 OK")
    void sendUpdate_when200_thenCorrect() {
        assertDoesNotThrow(() -> client.sendUpdate(
            new LinkUpdate(0L, new URI("https://github.com/"), "string", List.of())));
    }

    @Test
    @DisplayName("400")
    void sendUpdate_when400_thenException() {
        assertThatThrownBy(() -> client.sendUpdate(
            new LinkUpdate(0L, new URI("htt://github.com/"), "string", List.of())))
            .isInstanceOf(HttpClientErrorException.class)
            .hasMessage("400 Bot client encountered and error");
    }
}
