package edu.java.scrapper.domain;

import java.sql.Types;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class JdbcUsersDao {
    private final JdbcClient jdbcClient;

    @Transactional
    public long add(long id) {
        String sql = "INSERT INTO users VALUES (:id)";
        jdbcClient.sql(sql)
            .param("id", id, Types.BIGINT)
            .update();
        return id;
    }

    public void remove(long id) {
        String sql = "DELETE FROM users WHERE id = (:id)";
        jdbcClient.sql(sql)
            .param("id", id, Types.BIGINT)
            .update();
    }

    public List<Long> findAll() {
        String sql = "select * from users";
        return jdbcClient.sql(sql)
            .query(Long.class)
            .list();
    }

    public boolean isUserRegistered(long id) {
        String sql = "select exists (select 1 from users where id = (:id))";
        return Objects.requireNonNull(jdbcClient.sql(sql)
            .param("id", id)
            .query(Boolean.class)
            .single());
    }
}
