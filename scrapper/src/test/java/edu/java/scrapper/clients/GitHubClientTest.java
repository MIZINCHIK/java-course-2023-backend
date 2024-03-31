package edu.java.scrapper.clients;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.backoff.BackoffType;
import edu.java.model.exceptions.MalformedUrlException;
import edu.java.scrapper.clients.updates.github.Commit;
import edu.java.scrapper.clients.updates.github.GithubUpdate;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
            wmRuntimeInfo.getHttpBaseUrl(),
            BackoffType.EXPONENTIAL,
            Duration.of(1, ChronoUnit.SECONDS),
            10,
            List.of()
        );
    }

    @Test
    @DisplayName("getUpdate Correct path")
    void getUpdate_whenIsResponse_thenCorrect() {
        GithubUpdate update = client.getUpdate("owner1", "repo1");
        assertThat(update.updatedAt()).isEqualTo(OffsetDateTime.parse("2011-01-26T19:14:43Z"));
    }

    @Test
    @DisplayName("getUpdate No sought field")
    void getUpdate_whenNoSoughtField_thenEmpty() {
        assertThatThrownBy(() -> client.getUpdate("owner1", "repo3"))
            .isInstanceOf(HttpClientErrorException.class)
            .hasMessage("404 Github client encountered and error");
    }

    @Test
    @DisplayName("getUpdate By url and Correct path")
    void getUpdate_whenByUrlAndIsResponse_thenCorrect() {
        GithubUpdate update = client.getUpdate("https://github.com/owner1/repo1");
        assertThat(update.updatedAt()).isEqualTo(OffsetDateTime.parse("2011-01-26T19:14:43Z"));
    }

    @Test
    @DisplayName("getUpdate By url and No sought field")
    void getUpdate_whenByUrlAndNoSoughtField_thenEmpty() {
        assertThatThrownBy(() -> client.getUpdate("https://github.com/owner1/repo3"))
            .isInstanceOf(HttpClientErrorException.class)
            .hasMessage("404 Github client encountered and error");
    }

    @Test
    @DisplayName("getUpdate By url and incorrect url")
    void getUpdate_whenByUrlAndIncorrectUrl_thenMalformedUrlException() {
        assertThatThrownBy(() -> client.getUpdate("https://githb.com/owner1/repo3"))
            .isInstanceOf(MalformedUrlException.class);
    }

    @Test
    @DisplayName("getCommits Correct path")
    void getCommits_whenIsResponse_thenCorrect() {
        List<Commit> commits = client.getCommits("owner1", "repo1");
        assertThat(commits.size()).isEqualTo(2);
        assertThat(commits.getFirst().info().author().name()).isEqualTo("Maxim Chernyshov");
        assertThat(commits.getLast().info().author().name()).isEqualTo("MIZINCHIK");
        assertThat(commits.getFirst().info().author().date()).isEqualTo(OffsetDateTime.parse("2024-03-17T15:48:59Z"));
        assertThat(commits.getLast().info().author().date()).isEqualTo(OffsetDateTime.parse("2024-03-11T07:57:04Z"));
        assertThat(commits.getFirst().url().toString()).isEqualTo(
            "https://github.com/MIZINCHIK/java-course-2023-backend/commit/211473fd13092bfb33d77f6f074e03e5995d0041");
        assertThat(commits.getLast().url().toString()).isEqualTo(
            "https://github.com/MIZINCHIK/java-course-2023-backend/commit/1e265c877de9ef374acec38b070a8e7cd802223a");
    }

    @Test
    @DisplayName("getCommits No sought field")
    void getCommits_whenNoSoughtField_thenEmpty() {
        assertThatThrownBy(() -> client.getCommits("owner1", "repo3"))
            .isInstanceOf(HttpClientErrorException.class)
            .hasMessage("404 Github client encountered and error");
    }

    @Test
    @DisplayName("getCommits By url Correct path")
    void getCommits_whenByUrlAndIsResponse_thenCorrect() {
        List<Commit> commits = client.getCommits("https://github.com/owner1/repo1");
        assertThat(commits.size()).isEqualTo(2);
        assertThat(commits.getFirst().info().author().name()).isEqualTo("Maxim Chernyshov");
        assertThat(commits.getLast().info().author().name()).isEqualTo("MIZINCHIK");
        assertThat(commits.getFirst().info().author().date()).isEqualTo(OffsetDateTime.parse("2024-03-17T15:48:59Z"));
        assertThat(commits.getLast().info().author().date()).isEqualTo(OffsetDateTime.parse("2024-03-11T07:57:04Z"));
        assertThat(commits.getFirst().url().toString()).isEqualTo(
            "https://github.com/MIZINCHIK/java-course-2023-backend/commit/211473fd13092bfb33d77f6f074e03e5995d0041");
        assertThat(commits.getLast().url().toString()).isEqualTo(
            "https://github.com/MIZINCHIK/java-course-2023-backend/commit/1e265c877de9ef374acec38b070a8e7cd802223a");
    }

    @Test
    @DisplayName("getCommits By url No sought field")
    void getCommits_whenByUrlAndNoSoughtField_thenEmpty() {
        assertThatThrownBy(() -> client.getCommits("https://github.com/owner1/repo3"))
            .isInstanceOf(HttpClientErrorException.class)
            .hasMessage("404 Github client encountered and error");
    }

    @Test
    @DisplayName("getCommits By url and incorrect url")
    void getCommits_whenByUrlAndIncorrectUrl_thenMalformedUrlException() {
        assertThatThrownBy(() -> client.getCommits("https://githb.com/owner1/repo3"))
            .isInstanceOf(MalformedUrlException.class);
    }
}
