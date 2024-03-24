package edu.java.scrapper.domain;

import edu.java.model.links.Link;
import edu.java.model.links.LinkDomain;
import edu.java.scrapper.dto.LinkDto;
import java.sql.Types;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@SuppressWarnings("MultipleStringLiterals")
public class JdbcLinksDao {
    private final JdbcClient jdbcClient;

    public Long add(Link link) {
        String sql = "WITH inserting AS("
            + "INSERT INTO links (url, service, last_update) VALUES (:url, :service, NOW())"
            + " ON CONFLICT DO NOTHING RETURNING id)"
            + "SELECT id FROM inserting UNION SELECT id FROM links WHERE url=:url and service=:service;";
        return jdbcClient.sql(sql)
            .param("url", link.getUrl(), Types.VARCHAR)
            .param("service", link.getDomain().name, Types.OTHER)
            .query(Long.class)
            .single();
    }

    public void remove(long id) {
        String sql = "DELETE FROM links WHERE id = (:id)";
        jdbcClient.sql(sql)
            .param("id", id, Types.BIGINT)
            .update();
    }

    public List<LinkDto> findAll() {
        String sql = "select * from links";
        return jdbcClient.sql(sql)
            .query((rs, rowNum) -> new LinkDto(
                rs.getLong("id"),
                rs.getString("url"),
                LinkDomain.of(rs.getString("service")),
                OffsetDateTime.ofInstant(rs.getTimestamp("last_update").toInstant(), ZoneOffset.UTC)
            ))
            .list();
    }

    public List<LinkDto> findAllExpired(Duration expirationInterval) {
        String sql = "select * from links where last_update <= (:expiration_interval)";
        return jdbcClient.sql(sql)
            .param(
                "expiration_interval",
                OffsetDateTime.now(ZoneOffset.UTC).minus(expirationInterval),
                Types.TIMESTAMP_WITH_TIMEZONE
            )
            .query((rs, rowNum) -> new LinkDto(
                rs.getLong("id"),
                rs.getString("url"),
                LinkDomain.of(rs.getString("service")),
                OffsetDateTime.ofInstant(rs.getTimestamp("last_update").toInstant(), ZoneOffset.UTC)
            ))
            .list();
    }

    public void remove(String url) {
        String sql = "DELETE FROM links WHERE url = (:url)";
        jdbcClient.sql(sql)
            .param("url", url, Types.VARCHAR)
            .update();
    }

    public LinkDto findById(long linkId) {
        String sql = "select * from links where id = (:id)";
        return jdbcClient.sql(sql)
            .param("id", linkId)
            .query((rs, rowNum) -> new LinkDto(
                rs.getLong("id"),
                rs.getString("url"),
                LinkDomain.of(rs.getString("service")),
                OffsetDateTime.ofInstant(rs.getTimestamp("last_update").toInstant(), ZoneOffset.UTC)
            ))
            .single();
    }

    public Long findByUrl(String url) {
        String sql = "select id from links where url = (:url)";
        return jdbcClient.sql(sql)
            .param("url", url, Types.VARCHAR)
            .query(Long.class)
            .optional()
            .orElse(null);
    }

    public LinkDto update(long linkId, OffsetDateTime time) {
        String sql = "update links set last_update = (:time) where id = (:link_id) returning *";
        return jdbcClient.sql(sql)
            .param("time", time, Types.TIMESTAMP_WITH_TIMEZONE)
            .param("link_id", linkId, Types.BIGINT)
            .query((rs, rowNum) -> new LinkDto(
                rs.getLong("id"),
                rs.getString("url"),
                LinkDomain.of(rs.getString("service")),
                OffsetDateTime.ofInstant(rs.getTimestamp("last_update").toInstant(), ZoneOffset.UTC)
            ))
            .single();
    }
}
