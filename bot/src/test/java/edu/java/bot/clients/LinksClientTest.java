package edu.java.bot.clients;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.model.dto.LinkResponse;
import edu.java.model.dto.ListLinksResponse;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@WireMockTest
public class LinksClientTest {
    private static final String MAPPINGS = "src/test/resources/wiremock/links";
    private LinksClient client;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wmRuntimeInfo) {
        WireMock wireMock = wmRuntimeInfo.getWireMock();
        wireMock.loadMappingsFrom(MAPPINGS);
        client = new ClientsConfiguration().linksClient(
            WebClient.builder(),
            wmRuntimeInfo.getHttpBaseUrl()
        );
    }

    @Test
    @DisplayName("GET 200")
    void getLinks_when200_thenCorrect() {
        ListLinksResponse response = client.getLinks(0L);
        assertThat(response.links().isEmpty()).isTrue();
        assertThat(response.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("GET 400")
    void getLinks_when400_thenException() {
        assertThatThrownBy(() -> client.getLinks(1L))
            .isInstanceOf(HttpClientErrorException.class)
            .hasMessage("400 Links client encountered and error");
    }

    @Test
    @DisplayName("POST 200")
    void postLink_when200_thenCorrect() throws URISyntaxException {
        LinkResponse response = client.postLink(0L, new URI("https://github.com/"));
        assertThat(response.id()).isEqualTo(0L);
        assertThat(response.url().toString()).isEqualTo("https://github.com/");
    }

    @Test
    @DisplayName("POST 400")
    void postLink_when400_thenException() {
        assertThatThrownBy(() -> client.postLink(1L, new URI("https://github.com/")))
            .isInstanceOf(HttpClientErrorException.class)
            .hasMessage("400 Links client encountered and error");
    }

    @Test
    @DisplayName("DELETE 200")
    void deleteLink_when200_thenCorrect() throws URISyntaxException {
        LinkResponse response = client.deleteLink(0L, new URI("https://github.com/"));
        assertThat(response.id()).isEqualTo(0L);
        assertThat(response.url().toString()).isEqualTo("https://github.com/");
    }

    @Test
    @DisplayName("DELETE 400")
    void deleteLink_when400_thenException() {
        assertThatThrownBy(() -> client.postLink(1L, new URI("https://github.com/")))
            .isInstanceOf(HttpClientErrorException.class)
            .hasMessage("400 Links client encountered and error");
    }
}
