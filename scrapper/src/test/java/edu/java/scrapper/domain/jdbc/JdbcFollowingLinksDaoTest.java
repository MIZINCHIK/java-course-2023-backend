package edu.java.scrapper.domain.jdbc;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.jdbc.JdbcFollowingLinksDao;
import edu.java.scrapper.dto.FollowingData;
import edu.java.scrapper.dto.LinkDto;
import java.sql.Types;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class JdbcFollowingLinksDaoTest extends IntegrationTest {
    @Autowired
    JdbcClient jdbcClient;
    @Autowired
    JdbcFollowingLinksDao followingLinksRepository;

    private List<FollowingData> retrieveIds() {
        return jdbcClient.sql("select * from following_links")
            .query(FollowingData.class)
            .list()
            .stream()
            .map(id -> new FollowingData(id.userId(), id.linkId()))
            .toList();
    }

    private List<Long> retrieveLinksIds() {
        return jdbcClient.sql("select * from links")
            .query((rs, rowNum) -> new edu.java.scrapper.dto.LinkDto(
                rs.getLong("id"),
                rs.getString("url"),
                edu.java.model.links.LinkDomain.valueOf(rs.getString("service")),
                java.time.OffsetDateTime.of(rs.getTimestamp("last_update").toLocalDateTime(), java.time.ZoneOffset.UTC)
            ))
            .list()
            .stream()
            .map(LinkDto::id)
            .toList();
    }

    private void insertUserId(Long userId) {
        jdbcClient.sql("INSERT INTO users VALUES (:id)")
            .param("id", userId, Types.BIGINT)
            .update();
    }

    private Long insertLinkId() {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql("INSERT INTO links (url, service, last_update) VALUES (:url, :service, NOW()) RETURNING id")
            .param("url", RandomStringUtils.random(10))
            .param("service", ThreadLocalRandom.current().nextBoolean() ? "GitHub" : "StackOverflow", Types.OTHER)
            .update(keyHolder);
        return keyHolder.getKeyAs(Long.class);
    }

    private Long insertIds(Long userId) {
        insertUserId(userId);
        return insertLinkId();
    }

    @RepeatedTest(5)
    @Transactional
    @Rollback
    @DisplayName("Add when it's a duplicate")
    void add_whenIdPresentAlready_thenCorrect() {
        Long userId = ThreadLocalRandom.current().nextLong();
        Long linkId = insertIds(userId);
        jdbcClient.sql("INSERT INTO following_links (user_id, link_id) VALUES (:user_id, :link_id)")
            .param("user_id", userId, Types.BIGINT)
            .param("link_id", linkId, Types.BIGINT)
            .update();
        assertThat(followingLinksRepository.add(userId, linkId)).isEqualTo(new FollowingData(userId, linkId));
    }

    @RepeatedTest(5)
    @Transactional
    @Rollback
    @DisplayName("Add when no duplicates")
    void add_whenNewId_thenSuccess() {
        Long userId = ThreadLocalRandom.current().nextLong();
        Long linkId = insertIds(userId);
        FollowingData returnedId = followingLinksRepository.add(userId, linkId);
        assertThat(returnedId.userId()).isEqualTo(userId);
        assertThat(returnedId.linkId()).isEqualTo(linkId);
        var ids = retrieveIds();
        assertThat(ids.size()).isEqualTo(1);
        assertThat(ids.contains(new FollowingData(returnedId.userId(), returnedId.linkId()))).isTrue();
    }

    @RepeatedTest(5)
    @Transactional
    @Rollback
    @DisplayName("Add when no foreign keys")
    void add_whenBothIdsNotPresentInRequiredTables_thenRollback() {
        Random random = ThreadLocalRandom.current();
        assertThatThrownBy(() -> followingLinksRepository.add(
            random.nextLong(),
            random.nextLong()
        )).isInstanceOf(
            DataIntegrityViolationException.class);
    }

    @RepeatedTest(5)
    @Transactional
    @Rollback
    @DisplayName("Remove when id is not in the db")
    void remove_whenIdNotFound_thenSuccess() {
        Random random = ThreadLocalRandom.current();
        FollowingData pair = new FollowingData(random.nextLong(), random.nextLong());
        var ids = retrieveIds();
        assertThat(ids.contains(pair)).isFalse();
        assertDoesNotThrow(() -> followingLinksRepository.remove(pair.userId(), pair.linkId()));
        ids = retrieveIds();
        assertThat(ids.contains(pair)).isFalse();
    }

    @RepeatedTest(5)
    @Transactional
    @Rollback
    @DisplayName("Remove when id is in the db and the parent link has other followings")
    void remove_whenIdFoundAndNotLast_thenRemovedAndLinkItselfStays() {
        Random random = ThreadLocalRandom.current();
        long userId = random.nextLong();
        long secondUserId = userId == 0L ? 1L : 0L;
        FollowingData firstPair = new FollowingData(userId, insertIds(userId));
        insertUserId(secondUserId);
        String sql = "INSERT INTO following_links (user_id, link_id) VALUES (:user_id, :link_id)";
        jdbcClient.sql(sql)
            .param("user_id", userId, Types.BIGINT)
            .param("link_id", firstPair.linkId(), Types.BIGINT)
            .update();
        jdbcClient.sql(sql)
            .param("user_id", secondUserId, Types.BIGINT)
            .param("link_id", firstPair.linkId(), Types.BIGINT)
            .update();
        var ids = retrieveIds();
        assertThat(ids.contains(firstPair)).isTrue();
        assertDoesNotThrow(() -> followingLinksRepository.remove(firstPair.userId(), firstPair.linkId()));
        ids = retrieveIds();
        assertThat(ids.contains(firstPair)).isFalse();
        assertThat(retrieveLinksIds().contains(firstPair.linkId())).isTrue();
    }

    @RepeatedTest(5)
    @Transactional
    @Rollback
    @DisplayName("Remove when id is in the db and it's the last for the parent link")
    void remove_whenLastFollowingLinkRemoved_thenLinkItselfRemoved() {
        Random random = ThreadLocalRandom.current();
        long userId = random.nextLong();
        FollowingData firstPair = new FollowingData(userId, insertIds(userId));
        FollowingData secondPair = new FollowingData(userId, insertLinkId());
        jdbcClient.sql("INSERT INTO following_links (user_id, link_id) VALUES (:user_id, :link_id)")
            .param("user_id", userId, Types.BIGINT)
            .param("link_id", firstPair.linkId(), Types.BIGINT)
            .update();
        var ids = retrieveIds();
        assertThat(ids.contains(firstPair)).isTrue();
        assertDoesNotThrow(() -> followingLinksRepository.remove(firstPair.userId(), firstPair.linkId()));
        ids = retrieveIds();
        assertThat(ids.contains(firstPair)).isFalse();
        assertThat(retrieveLinksIds().contains(firstPair.linkId())).isFalse();
        assertThat(retrieveLinksIds().contains(secondPair.linkId())).isTrue();
    }

    @RepeatedTest(5)
    @Transactional
    @Rollback
    @DisplayName("Remove when parent user removed")
    void remove_whenParentUserRemoved_thenCascadeRemove() {
        Random random = ThreadLocalRandom.current();
        Long userId = random.nextLong();
        FollowingData pair = new FollowingData(userId, insertIds(userId));
        jdbcClient.sql("INSERT INTO following_links (user_id, link_id) VALUES (:user_id, :link_id)")
            .param("user_id", userId, Types.BIGINT)
            .param("link_id", pair.linkId(), Types.BIGINT)
            .update();
        var ids = retrieveIds();
        assertThat(ids.contains(pair)).isTrue();
        jdbcClient.sql("delete from users where id = (:id)")
            .param("id", userId, Types.BIGINT)
            .update();
        ids = retrieveIds();
        assertThat(ids.contains(pair)).isFalse();
    }

    @RepeatedTest(5)
    @Transactional
    @Rollback
    @DisplayName("Remove when parent link removed")
    void remove_whenParentLinkRemoved_thenCascadeRemove() {
        Random random = ThreadLocalRandom.current();
        Long userId = random.nextLong();
        FollowingData pair = new FollowingData(userId, insertIds(userId));
        jdbcClient.sql("INSERT INTO following_links (user_id, link_id) VALUES (:user_id, :link_id)")
            .param("user_id", userId, Types.BIGINT)
            .param("link_id", pair.linkId(), Types.BIGINT)
            .update();
        var ids = retrieveIds();
        assertThat(ids.contains(pair)).isTrue();
        jdbcClient.sql("delete from links where id = (:id)")
            .param("id", pair.linkId(), Types.BIGINT)
            .update();
        ids = retrieveIds();
        assertThat(ids.contains(pair)).isFalse();
    }

    @RepeatedTest(3)
    @Transactional
    @Rollback
    @DisplayName("Find all")
    void findByUserId_whenCalled_thenCorrect() {
        long userId1 = 1L;
        long userId2 = 2L;
        insertUserId(userId1);
        insertUserId(userId2);
        Long link1 = insertLinkId();
        Long link2 = insertLinkId();
        Long link3 = insertLinkId();
        List<FollowingData> followingLinks = List.of(
            new FollowingData(userId1, link1),
            new FollowingData(userId1, link2),
            new FollowingData(userId1, link3),
            new FollowingData(userId2, link1),
            new FollowingData(userId2, link3)
        );
        for (var data : followingLinks) {
            jdbcClient.sql(
                    "INSERT INTO following_links (user_id, link_id) VALUES (:user_id, :link_id)")
                .param("user_id", data.userId(), Types.BIGINT)
                .param("link_id", data.linkId(), Types.BIGINT)
                .update();
        }
        assertThat(followingLinksRepository.findByUserId(userId1)).usingRecursiveComparison()
            .isEqualTo(followingLinks.stream()
                .filter(data -> data.userId() == userId1)
                .toList());
        assertThat(followingLinksRepository.findByUserId(userId2)).usingRecursiveComparison()
            .isEqualTo(followingLinks.stream()
                .filter(data -> data.userId() == userId2)
                .toList());
    }

    @RepeatedTest(3)
    @Transactional
    @Rollback
    @DisplayName("Find all")
    void findByLinkId_whenCalled_thenCorrect() {
        long userId1 = 1L;
        long userId2 = 2L;
        insertUserId(userId1);
        insertUserId(userId2);
        Long link1 = insertLinkId();
        Long link2 = insertLinkId();
        Long link3 = insertLinkId();
        List<FollowingData> followingLinks = List.of(
            new FollowingData(userId1, link1),
            new FollowingData(userId1, link2),
            new FollowingData(userId1, link3),
            new FollowingData(userId2, link1),
            new FollowingData(userId2, link3)
        );
        for (var data : followingLinks) {
            jdbcClient.sql(
                    "INSERT INTO following_links (user_id, link_id) VALUES (:user_id, :link_id)")
                .param("user_id", data.userId(), Types.BIGINT)
                .param("link_id", data.linkId(), Types.BIGINT)
                .update();
        }
        assertThat(followingLinksRepository.findByLinkId(link1)).usingRecursiveComparison()
            .isEqualTo(followingLinks.stream()
                .filter(data -> data.linkId() == link1)
                .toList());
        assertThat(followingLinksRepository.findByLinkId(link2)).usingRecursiveComparison()
            .isEqualTo(followingLinks.stream()
                .filter(data -> data.linkId() == link2)
                .toList());
        assertThat(followingLinksRepository.findByLinkId(link3)).usingRecursiveComparison()
            .isEqualTo(followingLinks.stream()
                .filter(data -> data.linkId() == link3)
                .toList());
    }

    @RepeatedTest(3)
    @Transactional
    @Rollback
    @DisplayName("Find all by ids")
    void findByIds_whenCalled_thenCorrect() {
        long userId1 = 1L;
        long userId2 = 2L;
        insertUserId(userId1);
        insertUserId(userId2);
        Long link1 = insertLinkId();
        Long link2 = insertLinkId();
        Long link3 = insertLinkId();
        List<FollowingData> followingLinks = List.of(
            new FollowingData(userId1, link1),
            new FollowingData(userId1, link2),
            new FollowingData(userId1, link3),
            new FollowingData(userId2, link1),
            new FollowingData(userId2, link3)
        );
        for (var data : followingLinks) {
            jdbcClient.sql(
                    "INSERT INTO following_links (user_id, link_id) VALUES (:user_id, :link_id)")
                .param("user_id", data.userId(), Types.BIGINT)
                .param("link_id", data.linkId(), Types.BIGINT)
                .update();
        }
        assertThat(followingLinksRepository.findByIds(userId1, link1))
            .isEqualTo(followingLinks.getFirst());
        assertThat(followingLinksRepository.findByIds(userId1, link2))
            .isEqualTo(followingLinks.get(1));
        assertThat(followingLinksRepository.findByIds(userId1, link3))
            .isEqualTo(followingLinks.get(2));
        assertThat(followingLinksRepository.findByIds(userId2, link1))
            .isEqualTo(followingLinks.get(3));
        assertThat(followingLinksRepository.findByIds(userId2, link3))
            .isEqualTo(followingLinks.get(4));
        assertThat(followingLinksRepository.findByIds(userId2, link2))
            .isNull();
    }
}
