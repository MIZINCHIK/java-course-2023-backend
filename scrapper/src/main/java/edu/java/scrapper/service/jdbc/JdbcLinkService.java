package edu.java.scrapper.service.jdbc;

import edu.java.model.dto.LinkResponse;
import edu.java.model.links.Link;
import edu.java.scrapper.domain.jdbc.JdbcFollowingLinksDao;
import edu.java.scrapper.domain.jdbc.JdbcLinksDao;
import edu.java.scrapper.dto.FollowingData;
import edu.java.scrapper.dto.LinkDto;
import edu.java.scrapper.exceptions.LinkNotTrackedException;
import edu.java.scrapper.exceptions.UserNotRegisteredException;
import edu.java.scrapper.service.ModifiableLinkStorage;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JdbcLinkService implements ModifiableLinkStorage {
    private final JdbcLinksDao linksDao;
    private final JdbcFollowingLinksDao followingLinksDao;

    @Transactional
    @Override
    public List<LinkResponse> getLinksByUserId(long userId) {
        List<LinkResponse> links = new ArrayList<>();
        followingLinksDao.findByUserId(userId)
            .stream()
            .map(FollowingData::linkId)
            .forEach(id -> consumeId(id, links));
        return links;
    }

    private void consumeId(Long linkId, List<LinkResponse> result) {
        try {
            result.add(new LinkResponse(linkId, URI.create(linksDao.findById(linkId).url())));
        } catch (Exception ignored) {
        }
    }

    @Transactional
    @Override
    public long trackLink(Link link, long userId) {
        Long linkId = linksDao.add(link);
        try {
            return followingLinksDao.add(userId, linkId).linkId();
        } catch (DataIntegrityViolationException e) {
            throw new UserNotRegisteredException(e);
        }
    }

    @Transactional
    @Override
    public long untrackLink(Link link, long userId) {
        long linkId;
        try {
            linkId = linksDao.findByUrl(link.getUrl().toString());
        } catch (EmptyResultDataAccessException e) {
            throw new LinkNotTrackedException(e);
        }
        followingLinksDao.remove(userId, linkId);
        return linkId;
    }

    @Transactional
    @Override
    public boolean isLinkTracked(Link link, long userId) {
        Long linkId = linksDao.findByUrl(link.getUrl().toString());
        if (linkId == null) {
            return false;
        } else {
            return followingLinksDao.findByIds(userId, linkId) != null;
        }
    }

    @Override
    public List<LinkDto> getLinksWithExpiredCheckTime(Duration expirationInterval) {
        return linksDao.findAllExpired(expirationInterval);
    }

    @Override
    public void updateLink(long linkId, OffsetDateTime time) {
        try {
            linksDao.update(linkId, time);
        } catch (EmptyResultDataAccessException e) {
            throw new LinkNotTrackedException(e);
        }
    }

    @Transactional
    @Override
    public List<Long> getUsersByLink(Long id) {
        return followingLinksDao.findByLinkId(id).stream()
            .map(FollowingData::userId)
            .toList();
    }

    @Override
    public void removeLink(String url) {
        linksDao.remove(url);
    }
}
