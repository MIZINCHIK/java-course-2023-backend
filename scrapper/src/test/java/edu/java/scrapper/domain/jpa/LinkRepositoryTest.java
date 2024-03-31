package edu.java.scrapper.domain.jpa;

import edu.java.model.links.Link;
import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.jpa.entities.LinkEntity;
import edu.java.scrapper.domain.jpa.entities.UserEntity;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class LinkRepositoryTest extends IntegrationTest {
    @Autowired
    UserRepository usersRepository;
    @Autowired
    LinkRepository linksRepository;

    private List<LinkEntity> retrieveLinks() {
        return linksRepository.findAll();
    }

    private List<Long> retrieveIds() {
        return retrieveLinks().stream().map(LinkEntity::getId).toList();
    }

    private List<String> retrieveUrls() {
        return retrieveLinks().stream().map(LinkEntity::getUrl).toList();
    }

    @ParameterizedTest
    @Transactional
    @Rollback
    @MethodSource("generateLinkDtos")
    @DisplayName("Add no duplicates")
    void add_whenNoDuplicates_thenCorrect(String url) throws URISyntaxException {
        LinkEntity returnedLink = linksRepository.save(new LinkEntity(new Link(url)));
        assertThat(returnedLink).isNotNull();
        var links = retrieveLinks();
        assertThat(links.size()).isEqualTo(1);
        var link = links.getFirst();
        assertThat(link).isEqualTo(returnedLink);
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
    void add_whenDuplicates_thenReturnPreviousLink() throws URISyntaxException {
        Link link = new Link("https://stackoverflow.com/");
        LinkEntity savedEntity = new LinkEntity(link);
        linksRepository.save(savedEntity);
        assertThat(linksRepository.save(savedEntity)).isEqualTo(savedEntity);
    }

    @ParameterizedTest
    @Transactional
    @Rollback
    @CsvSource({"1", "7", "0", "12345", "-1234"})
    @DisplayName("Remove by id when id is not in the db")
    void remove_whenIdNotFound_thenSuccess(Long id) {
        var ids = retrieveIds();
        assertThat(ids.contains(id)).isFalse();
        assertDoesNotThrow(() -> linksRepository.deleteById(id));
        ids = retrieveIds();
        assertThat(ids.contains(id)).isFalse();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Remove by id when id is in the db")
    void remove_whenIdFound_thenRemoved() throws URISyntaxException {
        var id = linksRepository.save(new LinkEntity(new Link("https://www.github.com"))).getId();
        var ids = retrieveIds();
        assertThat(ids.contains(id)).isTrue();
        assertDoesNotThrow(() -> linksRepository.deleteById(id));
        ids = retrieveIds();
        assertThat(ids.contains(id)).isFalse();
    }

    @ParameterizedTest
    @Transactional
    @Rollback
    @CsvSource({"https://www.stackoverflow.com", "https://www.github.com"})
    @DisplayName("Remove by url when id is not in the db")
    void remove_whenUrlNotFound_thenSuccess(String url) {
        var urls = retrieveUrls();
        assertThat(urls.contains(url)).isFalse();
        assertDoesNotThrow(() -> linksRepository.deleteByUrl(url));
        urls = retrieveUrls();
        assertThat(urls.contains(url)).isFalse();
    }

    @ParameterizedTest
    @Transactional
    @Rollback
    @CsvSource({"https://www.stackoverflow.com", "https://www.github.com"})
    @DisplayName("Remove by url when id is in the db")
    void remove_whenUrlFound_thenRemoved(String url) throws URISyntaxException {
        linksRepository.save(new LinkEntity(new Link(url)));
        var urls = retrieveUrls();
        assertThat(urls.contains(url)).isTrue();
        assertDoesNotThrow(() -> linksRepository.deleteByUrl(url));
        urls = retrieveUrls();
        assertThat(urls.contains(url)).isFalse();
    }

    @ParameterizedTest
    @Transactional
    @Rollback
    @MethodSource("generateLinks")
    @DisplayName("Find all")
    void findAll_whenCalled_thenCorrect(Collection<LinkEntity> links) {
        assertThat(linksRepository.findAll().size()).isEqualTo(0);
        linksRepository.saveAll(links);
        var entities = linksRepository.findAll();
        entities.forEach(entity -> entity.setId(0L));
        assertThat(entities)
            .isEqualTo(links);
    }

    static Stream<Arguments> generateLinks() throws URISyntaxException {
        return Stream.of(
            Arguments.of(
                List.of(
                    new LinkEntity(new Link("https://www.stackoverflow.com/1")),
                    new LinkEntity(new Link("https://www.github.com/2")),
                    new LinkEntity(new Link("https://www.stackoverflow.com/3")),
                    new LinkEntity(new Link("https://www.stackoverflow.com/4")),
                    new LinkEntity(new Link("https://www.stackoverflow.com/5")),
                    new LinkEntity(new Link("https://www.github.com/6"))
                )
            )
        );
    }

    @ParameterizedTest
    @Transactional
    @Rollback
    @MethodSource("generateLinks")
    @DisplayName("Find all expired")
    void findAllExpired_whenCalled_thenCorrect(List<LinkEntity> links) {
        assertThat(linksRepository.findAllByLastUpdateLessThan(
                OffsetDateTime.now(ZoneOffset.UTC).minus(Duration.of(3L, ChronoUnit.DAYS)))
            .size()).isEqualTo(0);
        links.getFirst()
            .setLastUpdate(OffsetDateTime.now(ZoneOffset.UTC).minusDays(16).truncatedTo(ChronoUnit.SECONDS));
        links.get(1).setLastUpdate(OffsetDateTime.now(ZoneOffset.UTC).plusDays(1).truncatedTo(ChronoUnit.SECONDS));
        links.get(2).setLastUpdate(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS));
        links.get(3).setLastUpdate(OffsetDateTime.now(ZoneOffset.UTC).minusDays(3).truncatedTo(ChronoUnit.SECONDS));
        links.get(4).setLastUpdate(OffsetDateTime.now(ZoneOffset.UTC).minusDays(2).truncatedTo(ChronoUnit.SECONDS));
        links.get(5).setLastUpdate(OffsetDateTime.now(ZoneOffset.UTC).minusDays(1).truncatedTo(ChronoUnit.SECONDS));
        linksRepository.saveAll(links);
        var entities = linksRepository.findAllByLastUpdateLessThan(OffsetDateTime.now(ZoneOffset.UTC)
            .minus(Duration.of(2, ChronoUnit.DAYS)));
        entities.forEach(entity -> entity.setId(0L));
        assertThat(entities)
            .isEqualTo(links.stream()
                .filter(entity -> entity.getLastUpdate().plusDays(2L).isBefore(OffsetDateTime.now(ZoneOffset.UTC)))
                .toList());
    }

    @ParameterizedTest
    @MethodSource("generateLinkDtos")
    @Transactional
    @Rollback
    @DisplayName("Find by id when doesn't exist")
    void findById_whenDoesntExist_thenException(String url) throws URISyntaxException {
        Long returnedId = linksRepository.save(new LinkEntity(new Link(url))).getId();
        assertThat(linksRepository.findById(returnedId == 0L ? 1L : 0L)).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("generateLinkDtos")
    @Transactional
    @Rollback
    @DisplayName("Find by id when exists")
    void findById_whenExists_thenSuccess(String url) throws URISyntaxException {
        var linkEntity = new LinkEntity(new Link(url));
        Long id = linksRepository.save(linkEntity).getId();
        assertThat(linksRepository.findById(id).get()).isEqualTo(linkEntity);
    }

    @ParameterizedTest
    @MethodSource("generateLinkDtos")
    @Transactional
    @Rollback
    @DisplayName("Find by id when doesn't exist")
    void findByUrl_whenDoesntExist_thenException(String url) throws URISyntaxException {
        linksRepository.save(new LinkEntity(new Link(url)));
        assertThat(linksRepository.findByUrl("")).isNull();
    }

    @ParameterizedTest
    @MethodSource("generateLinkDtos")
    @Transactional
    @Rollback
    @DisplayName("Find by id when exists")
    void findByUrl_whenExists_thenSuccess(String url) throws URISyntaxException {
        linksRepository.save(new LinkEntity(new Link(url)));
        assertThat(linksRepository.findByUrl(url)).isNotNull();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Update successful")
    void update_whenCorrectIds_thenUpdated() throws URISyntaxException {
        var link = linksRepository.save(new LinkEntity(new Link("https://stackoverflow.com/")));
        OffsetDateTime timeBefore = OffsetDateTime.now(ZoneOffset.UTC).minusDays(1L);
        link.setLastUpdate(timeBefore);
        assertThat(linksRepository.findById(link.getId()).get().getLastUpdate()).isEqualTo(timeBefore);
    }

    @RepeatedTest(5)
    @Transactional
    @Rollback
    @DisplayName("Add manyToMany relation when it's a duplicate")
    void addManyToMany_whenIdPresentAlready_thenCorrect() throws URISyntaxException {
        UserEntity user = new UserEntity(ThreadLocalRandom.current().nextLong());
        usersRepository.save(user);
        var link = linksRepository.save(new LinkEntity(new Link("https://stackoverflow.com/")));
        user.addLink(link);
        assertThat(linksRepository.existsByIdAndUsers_Id(link.getId(), user.getId())).isFalse();
        usersRepository.save(user);
        assertThat(linksRepository.existsByIdAndUsers_Id(link.getId(), user.getId())).isTrue();
        user.addLink(link);
        assertThat(linksRepository.existsByIdAndUsers_Id(link.getId(), user.getId())).isTrue();
    }

    @RepeatedTest(5)
    @Transactional
    @Rollback
    @DisplayName("Add manyToMany when no duplicates")
    void addManyToMany_whenNewId_thenSuccess() throws URISyntaxException {
        UserEntity user = new UserEntity(ThreadLocalRandom.current().nextLong());
        usersRepository.save(user);
        var link = linksRepository.save(new LinkEntity(new Link("https://stackoverflow.com/")));
        assertThat(linksRepository.existsByIdAndUsers_Id(link.getId(), user.getId())).isFalse();
        link.getUsers().add(user);
        user.addLink(link);
        usersRepository.save(user);
        assertThat(linksRepository.existsByIdAndUsers_Id(link.getId(), user.getId())).isTrue();
    }

    @RepeatedTest(5)
    @Transactional
    @Rollback
    @DisplayName("Remove manyToMany when id is in the db and the parent link has other followings")
    void removeManyToMany_whenIdFoundAndNotLast_thenRemovedAndLinkItselfStays() throws URISyntaxException {
        Random random = ThreadLocalRandom.current();
        UserEntity user1 = new UserEntity(random.nextLong());
        UserEntity user2 = new UserEntity(random.nextLong());
        usersRepository.save(user1);
        usersRepository.save(user2);
        var link1 = linksRepository.save(new LinkEntity(new Link("https://stackoverflow.com/")));
        user1.addLink(link1);
        user2.addLink(link1);
        usersRepository.save(user1);
        usersRepository.save(user2);
        assertThat(linksRepository.existsByIdAndUsers_Id(link1.getId(), user1.getId())).isTrue();
        assertThat(linksRepository.existsByIdAndUsers_Id(link1.getId(), user2.getId())).isTrue();
        user1.getLinks().clear();
        assertDoesNotThrow(() -> usersRepository.save(user1));
        assertThat(linksRepository.existsByIdAndUsers_Id(link1.getId(), user1.getId())).isFalse();
        assertThat(linksRepository.existsByIdAndUsers_Id(link1.getId(), user2.getId())).isTrue();
    }

    @RepeatedTest(5)
    @Transactional
    @Rollback
    @DisplayName("Remove manyToMany when id is in the db and it's the last for the parent link")
    void removeManyToMany_whenLastFollowingLinkRemoved_thenLinkItselfRemoved() throws URISyntaxException {
        Random random = ThreadLocalRandom.current();
        UserEntity user1 = new UserEntity(random.nextLong());
        usersRepository.save(user1);
        var link1 = linksRepository.save(new LinkEntity(new Link("https://stackoverflow.com/")));
        var link1Id = link1.getId();
        var link2 = linksRepository.save(new LinkEntity(new Link("https://stackoverflow.com/2")));
        var link3 = linksRepository.save(new LinkEntity(new Link("https://stackoverflow.com/3")));
        var link4 = linksRepository.save(new LinkEntity(new Link("https://stackoverflow.com/4")));
        var link5 = linksRepository.save(new LinkEntity(new Link("https://stackoverflow.com/5")));
        user1.addLink(link1);
        user1.addLink(link2);
        user1.addLink(link3);
        user1.addLink(link4);
        user1.addLink(link5);
        usersRepository.save(user1);
        assertThat(linksRepository.existsByIdAndUsers_Id(link1.getId(), user1.getId())).isTrue();
        user1.removeLink(link1);
        user1.removeLink(link3);
        user1.removeLink(link4);
        user1.removeLink(link5);
        usersRepository.save(user1);
        assertThat(linksRepository.existsByIdAndUsers_Id(link1Id, user1.getId())).isFalse();
        assertThat(linksRepository.existsByIdAndUsers_Id(link2.getId(), user1.getId())).isTrue();
        assertThat(linksRepository.findAll().contains(link2)).isTrue();
        assertThat(linksRepository.findAll().contains(link1)).isFalse();
    }
}
