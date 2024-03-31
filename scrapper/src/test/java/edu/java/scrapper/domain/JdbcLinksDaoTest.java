package edu.java.scrapper.domain;

import edu.java.model.links.Link;
import edu.java.model.links.LinkDomain;
import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.jdbc.JdbcLinksDao;
import edu.java.scrapper.dto.LinkDto;
import java.sql.Types;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class JdbcLinksDaoTest extends IntegrationTest {
    @Autowired
    JdbcClient jdbcClient;
    @Autowired
    JdbcLinksDao linksRepository;

    private List<LinkDto> retrieveLinks() {
        return jdbcClient.sql("select * from links")
            .query((rs, rowNum) -> new LinkDto(
                rs.getLong("id"),
                rs.getString("url"),
                LinkDomain.of(rs.getString("service")),
                OffsetDateTime.of(rs.getTimestamp("last_update").toLocalDateTime(), ZoneOffset.UTC)
            ))
            .list();
    }

    private List<Long> retrieveIds() {
        return retrieveLinks().stream().map(LinkDto::id).toList();
    }

    private List<String> retrieveUrls() {
        return retrieveLinks().stream().map(LinkDto::url).toList();
    }

    @ParameterizedTest
    @Transactional
    @Rollback
    @MethodSource("generateLinkDtos")
    @DisplayName("Add no duplicates")
    void add_whenNoDuplicates_thenCorrect(String url) {
        Long returnedId = linksRepository.add(new Link(url));
        assertThat(returnedId).isNotNull();
        var links = retrieveLinks();
        assertThat(links.size()).isEqualTo(1);
        var link = links.getFirst();
        assertThat(link.id()).isEqualTo(returnedId);
    }

    private static Stream<Arguments> generateLinkDtos() {
        return Stream.of(
            Arguments.of("https://stackoverflow.com/"),
            Arguments.of("https://github.com")
        );
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Add duplicate")
    void add_whenDuplicates_thenReturnPreviousLink() {
        Link link = new Link("https://stackoverflow.com/");
        Long id = linksRepository.add(link);
        assertThat(linksRepository.add(link)).isEqualTo(id);
    }

    @ParameterizedTest
    @Transactional
    @Rollback
    @CsvSource({"1", "7", "0", "12345", "-1234"})
    @DisplayName("Remove by id when id is not in the db")
    void remove_whenIdNotFound_thenSuccess(Long id) {
        var ids = retrieveIds();
        assertThat(ids.contains(id)).isFalse();
        assertDoesNotThrow(() -> linksRepository.remove(id));
        ids = retrieveIds();
        assertThat(ids.contains(id)).isFalse();
    }

    @ParameterizedTest
    @Transactional
    @Rollback
    @CsvSource({"GitHub", "StackOverflow"})
    @DisplayName("Remove by id when id is in the db")
    void remove_whenIdFound_thenRemoved(String service) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql("INSERT INTO links (url, service, last_update) VALUES (:url, :service, NOW()) RETURNING id")
            .param("url", "sdfsad", Types.VARCHAR)
            .param("service", service, Types.OTHER)
            .update(keyHolder);
        Long id = keyHolder.getKeyAs(Long.class);
        var ids = retrieveIds();
        assertThat(ids.contains(id)).isTrue();
        assertDoesNotThrow(() -> linksRepository.remove(id));
        ids = retrieveIds();
        assertThat(ids.contains(id)).isFalse();
    }

    @ParameterizedTest
    @Transactional
    @Rollback
    @CsvSource({"asdasfd", "w4fwerfrc", "asdvse", "asdjskdsa", "yjykmnasc"})
    @DisplayName("Remove by url when id is not in the db")
    void remove_whenUrlNotFound_thenSuccess(String url) {
        var urls = retrieveUrls();
        assertThat(urls.contains(url)).isFalse();
        assertDoesNotThrow(() -> linksRepository.remove(url));
        urls = retrieveUrls();
        assertThat(urls.contains(url)).isFalse();
    }

    @ParameterizedTest
    @Transactional
    @Rollback
    @CsvSource({"GitHub", "StackOverflow"})
    @DisplayName("Remove by url when id is in the db")
    void remove_whenUrlFound_thenRemoved(String service) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql("INSERT INTO links (url, service, last_update) VALUES (:url, :service, NOW()) RETURNING url")
            .param("url", "sdfsad", Types.VARCHAR)
            .param("service", service, Types.OTHER)
            .update(keyHolder);
        String url = keyHolder.getKeyAs(String.class);
        var urls = retrieveUrls();
        assertThat(urls.contains(url)).isTrue();
        assertDoesNotThrow(() -> linksRepository.remove(url));
        urls = retrieveUrls();
        assertThat(urls.contains(url)).isFalse();
    }

    @ParameterizedTest
    @Transactional
    @Rollback
    @MethodSource("generateLinks")
    @DisplayName("Find all")
    void findAll_whenCalled_thenCorrect(Collection<LinkDto> links) {
        assertThat(linksRepository.findAll().size()).isEqualTo(0);
        for (var link : links) {
            jdbcClient.sql("INSERT INTO links (url, service, last_update) VALUES (:url, :service, :last_update)")
                .param("url", link.url(), Types.VARCHAR)
                .param("service", link.service().name, Types.OTHER)
                .param("last_update", link.lastUpdate(), Types.TIMESTAMP_WITH_TIMEZONE)
                .update();
        }
        assertThat(linksRepository.findAll()
            .stream()
            .map(dto -> new LinkDto(1, dto.url(), dto.service(), dto.lastUpdate()))
            .toList())
            .isEqualTo(links);
    }

    static Stream<Arguments> generateLinks() {
        return Stream.of(
            Arguments.of(
                List.of(
                    new LinkDto(
                        1,
                        "1",
                        LinkDomain.SOF,
                        OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
                    ),
                    new LinkDto(
                        1,
                        "2",
                        LinkDomain.GITHUB,
                        OffsetDateTime.now(ZoneOffset.UTC).minusDays(1L).truncatedTo(ChronoUnit.SECONDS)
                    ),
                    new LinkDto(
                        1,
                        "3",
                        LinkDomain.SOF,
                        OffsetDateTime.now(ZoneOffset.UTC).plusDays(1L).truncatedTo(ChronoUnit.SECONDS)
                    )
                )
            )
        );
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Find all expired")
    void findAllExpired_whenCalled_thenCorrect() {
        assertThat(linksRepository.findAllExpired(Duration.of(3L, ChronoUnit.DAYS)).size()).isEqualTo(0);
        List<LinkDto> links = List.of(
            new LinkDto(
                1,
                "asdsadsaddsa",
                LinkDomain.SOF,
                OffsetDateTime.now(ZoneOffset.UTC).minusDays(16).truncatedTo(ChronoUnit.SECONDS)
            ),
            new LinkDto(
                1,
                "asdsa",
                LinkDomain.SOF,
                OffsetDateTime.now(ZoneOffset.UTC).plusDays(1).truncatedTo(ChronoUnit.SECONDS)
            ),
            new LinkDto(
                1,
                "asdsadsgddsaddsa",
                LinkDomain.SOF,
                OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
            ),
            new LinkDto(
                1,
                "123",
                LinkDomain.SOF,
                OffsetDateTime.now(ZoneOffset.UTC).minusDays(3).truncatedTo(ChronoUnit.SECONDS)
            ),
            new LinkDto(
                1,
                "456",
                LinkDomain.SOF,
                OffsetDateTime.now(ZoneOffset.UTC).minusDays(2).truncatedTo(ChronoUnit.SECONDS)
            ),
            new LinkDto(
                1,
                "789",
                LinkDomain.SOF,
                OffsetDateTime.now(ZoneOffset.UTC).minusDays(1).truncatedTo(ChronoUnit.SECONDS)
            )
        );
        for (var link : links) {
            jdbcClient.sql("INSERT INTO links (url, service, last_update) VALUES (:url, :service, :last_update)")
                .param("url", link.url(), Types.VARCHAR)
                .param("service", link.service().name, Types.OTHER)
                .param("last_update", link.lastUpdate(), Types.TIMESTAMP_WITH_TIMEZONE)
                .update();
        }
        assertThat(linksRepository.findAllExpired(Duration.of(2, ChronoUnit.DAYS))
            .stream()
            .map(dto -> new LinkDto(1, dto.url(), dto.service(), dto.lastUpdate()))
            .toList())
            .isEqualTo(links.stream()
                .filter(dto -> dto.lastUpdate().plusDays(2L).isBefore(OffsetDateTime.now(ZoneOffset.UTC)))
                .toList());
    }

    @ParameterizedTest
    @MethodSource("generateLinkDtos")
    @Transactional
    @Rollback
    @DisplayName("Find by id when doesn't exist")
    void findById_whenDoesntExist_thenException(String url) {
        Long returnedId = linksRepository.add(new Link(url));
        assertThatThrownBy(() -> linksRepository.findById(returnedId == 0 ? 1 : 0)).isInstanceOf(
            EmptyResultDataAccessException.class);
    }

    @ParameterizedTest
    @MethodSource("generateLinkDtos")
    @Transactional
    @Rollback
    @DisplayName("Find by id when exists")
    void findById_whenExists_thenSuccess(String url) {
        Link link = new Link(url);
        OffsetDateTime time = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql(
                "INSERT INTO links (url, service, last_update) VALUES (:url, :service, :last_update) returning id")
            .param("url", link.getUrl(), Types.VARCHAR)
            .param("service", link.getDomain().name, Types.OTHER)
            .param("last_update", time, Types.TIMESTAMP_WITH_TIMEZONE)
            .update(keyHolder);
        Long id = keyHolder.getKeyAs(Long.class);
        assertThat(linksRepository.findById(Objects.requireNonNull(id))).isEqualTo(new LinkDto(
            id,
            url,
            link.getDomain(),
            time
        ));
    }

    @ParameterizedTest
    @MethodSource("generateLinkDtos")
    @Transactional
    @Rollback
    @DisplayName("Find by id when doesn't exist")
    void findByUrl_whenDoesntExist_thenException(String url) {
        linksRepository.add(new Link(url));
        assertThat(linksRepository.findByUrl("")).isNull();
    }

    @ParameterizedTest
    @MethodSource("generateLinkDtos")
    @Transactional
    @Rollback
    @DisplayName("Find by id when exists")
    void findByUrl_whenExists_thenSuccess(String url) {
        linksRepository.add(new Link(url));
        assertDoesNotThrow(() -> linksRepository.findByUrl(url));
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Update ids not found")
    void update_whenIdDoesNotExist_thenException() {
        assertThatThrownBy(() -> linksRepository.update(0L, OffsetDateTime.now())).isInstanceOf(
            EmptyResultDataAccessException.class);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Update successful")
    void update_whenCorrectIds_thenUpdated() {
        long id = linksRepository.add(new Link("https://stackoverflow.com/"));
        OffsetDateTime timeBefore = OffsetDateTime.now(ZoneOffset.UTC).minusDays(1L);
        var updatedTime = linksRepository.update(id, OffsetDateTime.now(ZoneOffset.UTC)).lastUpdate();
        assertThat(updatedTime.isAfter(timeBefore)).isTrue();
    }
}
