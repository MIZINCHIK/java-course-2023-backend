package edu.java.scrapper.service.jooq;

import edu.java.model.dto.LinkResponse;
import edu.java.model.links.Link;
import edu.java.model.links.LinkDomain;
import edu.java.scrapper.domain.jooq.enums.ExternalService;
import edu.java.scrapper.dto.LinkDto;
import edu.java.scrapper.exceptions.LinkNotTrackedException;
import edu.java.scrapper.exceptions.UserNotRegisteredException;
import edu.java.scrapper.service.ModifiableLinkStorage;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.scrapper.domain.jooq.tables.FollowingLinks.FOLLOWING_LINKS;
import static edu.java.scrapper.domain.jooq.tables.Links.LINKS;

@Service
@RequiredArgsConstructor
public class JooqLinkService implements ModifiableLinkStorage {
    private final DSLContext dslContext;

    @Transactional
    @Override
    public List<LinkResponse> getLinksByUserId(long userId) {
        List<LinkResponse> links = new ArrayList<>();
        Map<Long, String> urls = dslContext.select(LINKS.ID, LINKS.URL)
            .from(FOLLOWING_LINKS)
            .join(LINKS)
            .on(FOLLOWING_LINKS.USER_ID.eq(userId))
            .and(LINKS.ID.eq(FOLLOWING_LINKS.LINK_ID))
            .fetchMap(LINKS.ID, LINKS.URL);
        for (long id : urls.keySet()) {
            try {
                links.add(new LinkResponse(id, URI.create(urls.get(id))));
            } catch (Exception ignored) {
            }
        }
        return links;
    }

    @Transactional
    @Override
    public long trackLink(Link link, long userId) {
        Long id;
        try {
            id = dslContext.insertInto(LINKS, LINKS.URL, LINKS.SERVICE, LINKS.LAST_UPDATE)
                .values(link.getUrl().toString(), ExternalService.valueOf(link.getDomain().name), OffsetDateTime.now(
                    ZoneOffset.UTC))
                .onDuplicateKeyIgnore()
                .returningResult(LINKS.ID)
                .fetchSingle(LINKS.ID, Long.class);
        } catch (NoDataFoundException ignored) {
            id = getLinkId(link);
        }
        try {
            dslContext.insertInto(FOLLOWING_LINKS, FOLLOWING_LINKS.LINK_ID, FOLLOWING_LINKS.USER_ID)
                .values(id, userId)
                .onDuplicateKeyIgnore()
                .execute();
            return Objects.requireNonNull(id);
        } catch (DataIntegrityViolationException e) {
            throw new UserNotRegisteredException(e);
        }
    }

    private Long getLinkId(Link link) {
        return dslContext.select()
            .from(LINKS)
            .where(LINKS.URL.eq(link.getUrl().toString()))
            .fetchOne(LINKS.ID, Long.class);
    }

    @Transactional
    @Override
    public long untrackLink(Link link, long userId) {
        Long linkId = getLinkId(link);
        if (linkId == null) {
            throw new LinkNotTrackedException();
        }
        dslContext.deleteFrom(FOLLOWING_LINKS)
            .where(FOLLOWING_LINKS.USER_ID.eq(userId))
            .and(FOLLOWING_LINKS.LINK_ID.eq(linkId))
            .execute();
        return linkId;
    }

    @Override
    public boolean isLinkTracked(Link link, long userId) {
        return dslContext.fetchExists(
            dslContext.selectOne().from(FOLLOWING_LINKS)
                .where(FOLLOWING_LINKS.USER_ID.eq(userId))
                .and(FOLLOWING_LINKS.LINK_ID.eq(
                    getLinkId(link)
                ))
        );
    }

    @Transactional
    @Override
    public List<LinkDto> getLinksWithExpiredCheckTime(Duration expirationInterval) {
        return dslContext.selectFrom(LINKS)
            .where(LINKS.LAST_UPDATE.lessOrEqual(OffsetDateTime.now(ZoneOffset.UTC).minus(expirationInterval)))
            .stream()
            .map(record -> new LinkDto(
                Objects.requireNonNull(record.getId()),
                record.getUrl(),
                LinkDomain.of(record.getService().getLiteral()),
                record.getLastUpdate()
            ))
            .toList();
    }

    @Override
    public void updateLink(long linkId, OffsetDateTime time) {
        try {
            dslContext.update(LINKS)
                .set(LINKS.LAST_UPDATE, time)
                .returning(LINKS.ID)
                .fetchSingle();
        } catch (NoDataFoundException e) {
            throw new LinkNotTrackedException(e);
        }
    }

    @Override
    public List<Long> getUsersByLink(Long id) {
        return dslContext.selectFrom(FOLLOWING_LINKS)
            .where(FOLLOWING_LINKS.LINK_ID.eq(id))
            .fetch(FOLLOWING_LINKS.USER_ID);
    }

    @Override
    public void removeLink(String url) {
        dslContext.deleteFrom(LINKS)
            .where(LINKS.URL.eq(url))
            .execute();
    }
}
