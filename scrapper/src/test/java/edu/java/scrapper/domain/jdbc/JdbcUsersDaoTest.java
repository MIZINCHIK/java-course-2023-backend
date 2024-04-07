package edu.java.scrapper.domain.jdbc;

import edu.java.scrapper.IntegrationTest;
import java.sql.Types;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import edu.java.scrapper.domain.jdbc.JdbcUsersDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class JdbcUsersDaoTest extends IntegrationTest {
    @Autowired
    private JdbcClient jdbcClient;
    @Autowired
    private JdbcUsersDao usersRepository;

    private List<Long> retrieveIds() {
        return jdbcClient.sql("select * from users")
            .query(Long.class)
            .list();
    }

    @ParameterizedTest
    @Transactional
    @Rollback
    @CsvSource({"1", "7", "0", "12345", "-1234"})
    @DisplayName("Add when it's a duplicate")
    void add_whenIdPresentAlready_thenRollback(Long id) {
        jdbcClient.sql("INSERT INTO users VALUES (:id)")
            .param("id", id, Types.BIGINT)
            .update();
        assertThatThrownBy(() -> usersRepository.add(id)).isInstanceOf(DuplicateKeyException.class);
    }

    @ParameterizedTest
    @Transactional
    @Rollback
    @CsvSource({"1", "7", "0", "12345", "-1234"})
    @DisplayName("Add when no duplicates")
    void add_whenNewId_thenSuccess(Long id) {
        Long returnedId = usersRepository.add(id);
        assertThat(returnedId).isEqualTo(id);
        var ids = retrieveIds();
        assertThat(ids.size()).isEqualTo(1);
        assertThat(ids.contains(id)).isTrue();
    }

    @ParameterizedTest
    @Transactional
    @Rollback
    @CsvSource({"1", "7", "0", "12345", "-1234"})
    @DisplayName("Remove when id is not in the db")
    void remove_whenIdNotFound_thenSuccess(Long id) {
        var ids = retrieveIds();
        assertThat(ids.contains(id)).isFalse();
        assertDoesNotThrow(() -> usersRepository.remove(id));
        ids = retrieveIds();
        assertThat(ids.contains(id)).isFalse();
    }

    @ParameterizedTest
    @Transactional
    @Rollback
    @CsvSource({"1", "7", "0", "12345", "-1234"})
    @DisplayName("Remove when id is in the db")
    void remove_whenIdFound_thenRemoved(Long id) {
        jdbcClient.sql("INSERT INTO users VALUES (:id)")
            .param("id", id, Types.BIGINT)
            .update();
        var ids = retrieveIds();
        assertThat(ids.contains(id)).isTrue();
        assertDoesNotThrow(() -> usersRepository.remove(id));
        ids = retrieveIds();
        assertThat(ids.contains(id)).isFalse();
    }

    @ParameterizedTest
    @Transactional
    @Rollback
    @MethodSource("generateIds")
    @DisplayName("Find all")
    void findAll_whenCalled_thenCorrect(Collection<Long> ids) {
        for (var id : ids) {
            jdbcClient.sql("INSERT INTO users VALUES (:id)")
                .param("id", id, Types.BIGINT)
                .update();
        }
        assertThat(usersRepository.findAll()).isEqualTo(ids);
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
        usersRepository.add(userId);
        long idToCheck = userId == 0 ? 0 : 1;
        assertThat(usersRepository.isUserRegistered(idToCheck)).isFalse();
    }

    @RepeatedTest(5)
    @Transactional
    @Rollback
    @DisplayName("User exists")
    void isUserRegistered_whenTrue_thenTrue() {
        Random random = ThreadLocalRandom.current();
        long userId = random.nextLong();
        usersRepository.add(userId);
        assertThat(usersRepository.isUserRegistered(userId)).isTrue();
    }
}
