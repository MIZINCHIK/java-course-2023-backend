package edu.java.scrapper.clients;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.backoff.BackoffType;
import edu.java.scrapper.clients.updates.stackoverflow.StackOverflowAnswer;
import edu.java.scrapper.clients.updates.stackoverflow.StackOverflowUpdate;
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
public class StackOverflowClientTest {
    private static final String MAPPINGS = "src/test/resources/wiremock/stackoverflow";
    private StackOverflowClient client;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wmRuntimeInfo) {
        WireMock wireMock = wmRuntimeInfo.getWireMock();
        wireMock.loadMappingsFrom(MAPPINGS);
        client = new ClientsConfiguration().stackOverflowClient(
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
    void getUpdate_whenExists_thenTrue() {
        StackOverflowUpdate update = client.getUpdate("75844487");
        assertThat(update.lastActivityDate()).isEqualTo(OffsetDateTime.parse("2023-04-01T22:53:27Z"));
    }

    @Test
    @DisplayName("getUpdate No sought field")
    void getUpdate_whenNoSoughtField_thenEmpty() {
        assertThatThrownBy(() -> client.getUpdate("1"))
            .isInstanceOf(HttpClientErrorException.class)
            .hasMessage("404 StackOverflow client encountered and error");
    }

    @Test
    @DisplayName("getUpdateByUrl Correct path by url")
    void getUpdateByUrl_whenExists_thenTrue() {
        StackOverflowUpdate update = client.getUpdateByUrl("https://www.stackoverflow.com/questions/75844487");
        assertThat(update.lastActivityDate()).isEqualTo(OffsetDateTime.parse("2023-04-01T22:53:27Z"));
    }

    @Test
    @DisplayName("getUpdateByUrl No sought field by url")
    void getUpdateByUrl_whenNoSoughtField_thenEmpty() {
        assertThatThrownBy(() -> client.getUpdateByUrl("https://www.stackoverflow.com/questions/1"))
            .isInstanceOf(HttpClientErrorException.class)
            .hasMessage("404 StackOverflow client encountered and error");
    }

    @Test
    @DisplayName("getUpdateByUrl Incorrect url")
    void getUpdateByUrl_whenIncorrectUrl_thenMalformedUrlException() {
        assertThatThrownBy(() -> client.getUpdateByUrl("https://www.stackoverflow.com/questions/1"))
            .isInstanceOf(HttpClientErrorException.class)
            .hasMessage("404 StackOverflow client encountered and error");
    }

    @Test
    @DisplayName("getAsnwer Correct path")
    void getAsnwer_whenExists_thenTrue() {
        List<StackOverflowAnswer> answers = client.getAnswerList("75844487");
        assertThat(answers.size()).isEqualTo(2);
        assertThat(answers.getFirst().creationDate()).isEqualTo(OffsetDateTime.parse("2008-08-23T13:19:45Z"));
        assertThat(answers.getLast().creationDate()).isEqualTo(OffsetDateTime.parse("2008-08-19T21:04:10Z"));
        assertThat(answers.getFirst().owner().link().toString()).isEqualTo(
            "https://stackoverflow.com/users/2597/tom-lokhorst");
        assertThat(answers.getLast().owner().link()
            .toString()).isEqualTo("https://stackoverflow.com/users/1311/pascal");
        assertThat(answers.getFirst().owner().name()).isEqualTo("Tom Lokhorst");
        assertThat(answers.getLast().owner().name()).isEqualTo("Pascal");
        assertThat(answers.getFirst().owner().userId()).isEqualTo(2597L);
        assertThat(answers.getLast().owner().userId()).isEqualTo(1311L);
        assertThat(answers.getFirst().answerId()).isEqualTo(24219L);
        assertThat(answers.getLast().answerId()).isEqualTo(16951L);
    }

    @Test
    @DisplayName("getAsnwer No sought field")
    void getAsnwer_whenNoSoughtField_thenEmpty() {
        assertThatThrownBy(() -> client.getAnswers("1"))
            .isInstanceOf(HttpClientErrorException.class)
            .hasMessage("404 StackOverflow client encountered and error");
    }
}
