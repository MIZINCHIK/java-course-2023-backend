package edu.java.scrapper.clients;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.scrapper.clients.updates.StackOverflowUpdate;
import edu.java.scrapper.configuration.ApplicationConfig;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@WireMockTest
public class StackOverflowClientTest {
    private static final String MAPPINGS = "src/test/resources/wiremock/stackoverflow";
    private StackOverflowClient client;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wmRuntimeInfo) {
        WireMock wireMock = wmRuntimeInfo.getWireMock();
        wireMock.loadMappingsFrom(MAPPINGS);
        client = new ClientsConfiguration().stackOverflowClient(
            WebClient.builder(),
            new ApplicationConfig(null,
                null, new ApplicationConfig.StackOverflow(wmRuntimeInfo.getHttpBaseUrl())
            )
        );
    }

    @Test
    @DisplayName("Correct path")
    void questionExists_whenExists_thenTrue() {
        StackOverflowUpdate update = client.getUpdate("75844487");
        assertThat(update.lastActivityDate()).isEqualTo(OffsetDateTime.parse("2023-04-01T22:53:27Z"));
    }

    @Test
    @DisplayName("No sought field")
    void getUpdate_whenNoSoughtField_thenEmpty() {
        assertThatThrownBy(() -> client.getUpdate("1"))
            .isInstanceOf(HttpClientErrorException.class)
            .hasMessage("404 StackOverflow client encountered and error");
    }
}
