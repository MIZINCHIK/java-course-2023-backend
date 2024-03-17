package edu.java.scrapper.domain;

import edu.java.scrapper.dto.FollowingData;
import java.sql.Types;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@SuppressWarnings("MultipleStringLiterals")
public class JdbcFollowingLinksDao {
    private final JdbcClient jdbcClient;

    public FollowingData add(long userId, long linkId) {
        String sql =
            "INSERT INTO following_links (user_id, link_id) VALUES (:user_id, :link_id)";
        jdbcClient.sql(sql)
            .param("user_id", userId, Types.BIGINT)
            .param("link_id", linkId, Types.BIGINT)
            .update();
        return new FollowingData(
            userId,
            linkId
        );
    }

    public void remove(long userId, long linkId) {
        String sql = "DELETE FROM following_links WHERE user_id = (:user_id) AND link_id = (:link_id)";
        jdbcClient.sql(sql)
            .param("user_id", userId, Types.BIGINT)
            .param("link_id", linkId, Types.BIGINT)
            .update();
    }

    public List<FollowingData> findByUserId(long userId) {
        String sql = "select * from following_links where user_id = (:user_id)";
        return jdbcClient.sql(sql)
            .param("user_id", userId, Types.BIGINT)
            .query((rs, rowNum) -> new FollowingData(
                rs.getLong("user_id"),
                rs.getLong("link_id")
            ))
            .list();
    }

    public List<FollowingData> findByLinkId(long linkId) {
        String sql = "select * from following_links where link_id = (:link_id)";
        return jdbcClient.sql(sql)
            .param("link_id", linkId, Types.BIGINT)
            .query((rs, rowNum) -> new FollowingData(
                rs.getLong("user_id"),
                rs.getLong("link_id")
            ))
            .list();
    }

    public FollowingData findByIds(long userId, long linkId) {
        String sql = "select * from following_links where user_id = (:user_id) and link_id = (:link_id)";
        return jdbcClient.sql(sql)
            .param("user_id", userId, Types.BIGINT)
            .param("link_id", linkId, Types.BIGINT)
            .query((rs, rowNum) -> new FollowingData(
                rs.getLong("user_id"),
                rs.getLong("link_id")
            ))
            .single();
    }
}
