package edu.java.scrapper.clients;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.scrapper.clients.updates.GithubUpdate;
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
public class GitHubClientTest {
    private static final String MAPPINGS = "src/test/resources/wiremock/github";
    private GitHubClient client;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wmRuntimeInfo) {
        WireMock wireMock = wmRuntimeInfo.getWireMock();
        wireMock.loadMappingsFrom(MAPPINGS);
        client = new ClientsConfiguration().gitHubClient(
            WebClient.builder(),
            wmRuntimeInfo.getHttpBaseUrl()
        );
    }

    @Test
    @DisplayName("Correct path")
    void getUpdate_whenIsResponse_thenCorrect() {
        GithubUpdate update = client.getUpdate("owner1", "repo1");
        assertThat(update.updatedAt()).isEqualTo(OffsetDateTime.parse("2011-01-26T19:14:43Z"));
    }

    @Test
    @DisplayName("No sought field")
    void getUpdate_whenNoSoughtField_thenEmpty() {
        assertThatThrownBy(() -> client.getUpdate("owner1", "repo3"))
            .isInstanceOf(HttpClientErrorException.class)
            .hasMessage("404 Github client encountered and error");
    }
}
