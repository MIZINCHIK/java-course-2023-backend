package edu.java.scrapper.domain.jpa;

import edu.java.model.links.Link;
import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.jpa.entities.LinkEntity;
import edu.java.scrapper.domain.jpa.entities.UserEntity;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
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

public class UserRepositoryTest extends IntegrationTest {
    @Autowired
    UserRepository usersRepository;
    @Autowired
    LinkRepository linkRepository;

    @ParameterizedTest
    @Transactional
    @Rollback
    @CsvSource({"1", "7", "0", "12345", "-1234"})
    @DisplayName("Add when it's a duplicate")
    void add_whenIdPresentAlready_thenDoNothing(Long id) {
        usersRepository.saveAndFlush(new UserEntity(id));
        assertDoesNotThrow(() -> usersRepository.save(new UserEntity(id)).getId());
    }

    @ParameterizedTest
    @Transactional
    @Rollback
    @CsvSource({"1", "7", "0", "12345", "-1234"})
    @DisplayName("Add when no duplicates")
    void add_whenNewId_thenSuccess(Long id) {
        UserEntity returnedEntity = usersRepository.save(new UserEntity(id));
        assertThat(returnedEntity.getId()).isEqualTo(id);
        List<UserEntity> entities = usersRepository.findAll();
        assertThat(entities.size()).isEqualTo(1);
        assertThat(entities.contains(returnedEntity)).isTrue();
    }

    @ParameterizedTest
    @Transactional
    @Rollback
    @CsvSource({"1", "7", "0", "12345", "-1234"})
    @DisplayName("Remove when id is not in the db")
    void remove_whenIdNotFound_thenSuccess(Long id) {
        var ids = retrieveIds();
        assertThat(ids.contains(id)).isFalse();
        assertDoesNotThrow(() -> usersRepository.deleteById(id));
        ids = retrieveIds();
        assertThat(ids.contains(id)).isFalse();
    }

    private List<Long> retrieveIds() {
        return usersRepository.findAll().stream()
            .map(UserEntity::getId)
            .toList();
    }

    @ParameterizedTest
    @Transactional
    @Rollback
    @CsvSource({"1", "7", "0", "12345", "-1234"})
    @DisplayName("Remove when id is in the db")
    void remove_whenIdFound_thenRemoved(Long id) {
        usersRepository.save(new UserEntity(id));
        var ids = retrieveIds();
        assertThat(ids.contains(id)).isTrue();
        assertDoesNotThrow(() -> usersRepository.deleteById(id));
        ids = retrieveIds();
        assertThat(ids.contains(id)).isFalse();
    }

    @ParameterizedTest
    @Transactional
    @Rollback
    @MethodSource("generateIds")
    @DisplayName("Find all")
    void findAll_whenCalled_thenCorrect(Collection<Long> ids) {
        var users = ids.stream()
            .map(UserEntity::new)
            .toList();
        usersRepository.saveAll(users);
        assertThat(usersRepository.findAll()).isEqualTo(users);
    }

    static Stream<Arguments> generateIds() {
        return Stream.of(Arguments.of(List.of(1L, 7L, 0L, 12345L, -1234L)));
    }

    @RepeatedTest(5)
    @Transactional
    @Rollback
    @DisplayName("User doesn't exist")
    void isUserRegistered_whenFalse_thenFalse() {
        Random random = ThreadLocalRandom.current();
        long userId = random.nextLong();
        usersRepository.save(new UserEntity(userId));
        long idToCheck = userId == 0 ? 0 : 1;
        assertThat(usersRepository.existsById(idToCheck)).isFalse();
    }

    @RepeatedTest(5)
    @Transactional
    @Rollback
    @DisplayName("User exists")
    void isUserRegistered_whenTrue_thenTrue() {
        Random random = ThreadLocalRandom.current();
        long userId = random.nextLong();
        usersRepository.save(new UserEntity(userId));
        assertThat(usersRepository.existsById(userId)).isTrue();
    }

    @RepeatedTest(3)
    @Transactional
    @Rollback
    @DisplayName("Find all")
    void findByLinkId_whenCalled_thenCorrect() throws URISyntaxException {
        long userId1 = 1L;
        long userId2 = 2L;
        var user1 = usersRepository.save(new UserEntity(userId1));
        var user2 = usersRepository.save(new UserEntity(userId2));
        LinkEntity link1 = linkRepository.save(new LinkEntity(new Link("https://www.github.com/1")));
        LinkEntity link2 = linkRepository.save(new LinkEntity(new Link("https://www.github.com/2")));
        LinkEntity link3 = linkRepository.save(new LinkEntity(new Link("https://www.stackoverflow.com/3")));
        linkRepository.save(link1);
        linkRepository.save(link2);
        linkRepository.save(link3);
        user1.addLink(link1);
        user2.addLink(link1);
        user1.addLink(link2);
        user1.addLink(link3);
        user2.addLink(link3);
        assertThat(usersRepository.findAllByLinks_Id(link1.getId())).usingRecursiveComparison()
            .isEqualTo(List.of(user1, user2));
        assertThat(usersRepository.findAllByLinks_Id(link2.getId())).usingRecursiveComparison()
            .isEqualTo(List.of(user1));
        assertThat(usersRepository.findAllByLinks_Id(link3.getId())).usingRecursiveComparison()
            .isEqualTo(List.of(user1, user2));
    }
}
