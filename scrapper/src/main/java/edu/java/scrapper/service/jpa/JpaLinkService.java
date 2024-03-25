package edu.java.scrapper.service.jpa;

import edu.java.model.dto.LinkResponse;
import edu.java.model.exceptions.MalformedUrlException;
import edu.java.model.links.Link;
import edu.java.scrapper.domain.jpa.LinkRepository;
import edu.java.scrapper.domain.jpa.UserRepository;
import edu.java.scrapper.domain.jpa.entities.LinkEntity;
import edu.java.scrapper.domain.jpa.entities.UserEntity;
import edu.java.scrapper.dto.LinkDto;
import edu.java.scrapper.exceptions.LinkNotTrackedException;
import edu.java.scrapper.exceptions.UserNotRegisteredException;
import edu.java.scrapper.service.ModifiableLinkStorage;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JpaLinkService implements ModifiableLinkStorage {
    private final LinkRepository linkRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public List<LinkResponse> getLinksByUserId(long userId) {
        List<LinkResponse> links = new ArrayList<>();
        linkRepository.findAllByUsers_Id(userId)
                .forEach(entity -> consume(entity, links));
        return links;
    }

    private void consume(LinkEntity linkEntity, List<LinkResponse> result) {
        try {
            result.add(new LinkResponse(linkEntity.getId(), URI.create(linkEntity.getUrl())));
        } catch (Exception ignored) {
        }
    }

    @Override
    @Transactional
    public long trackLink(Link link, long userId) {
        LinkEntity linkEntity = linkRepository.findByUrl(link.getUrl().toString());
        if (linkEntity == null) {
            try {
                linkEntity = linkRepository.save(new LinkEntity(link));
            } catch (URISyntaxException e) {
                throw new MalformedUrlException(e);
            }
        }
        UserEntity userEntity = userRepository.findById(userId).orElse(null);
        if (userEntity == null) {
            throw new UserNotRegisteredException();
        }
        if (!linkRepository.existsByIdAndUsers_Id(linkEntity.getId(), userId)) {
            userEntity.addLink(linkEntity);
        }
        return linkRepository.save(linkEntity).getId();
    }

    @Override
    @Transactional
    public long untrackLink(Link link, long userId) {
        LinkEntity linkEntity = linkRepository.findByUrl(link.getUrl().toString());
        if (linkEntity == null) {
            throw new LinkNotTrackedException();
        }
        userRepository.findById(userId).ifPresent(
                user -> {
                    user.removeLink(linkEntity);
                    userRepository.save(user);
                }
        );
        return linkEntity.getId();
    }

    @Override
    @Transactional
    public boolean isLinkTracked(Link link, long userId) {
        LinkEntity linkEntity = linkRepository.findByUrl(link.getUrl().toString());
        return linkEntity != null && linkRepository.existsByIdAndUsers_Id(linkEntity.getId(), userId);
    }

    @Override
    @Transactional
    public List<LinkDto> getLinksWithExpiredCheckTime(Duration expirationInterval) {
        return linkRepository.findAllByLastUpdateLessThan(OffsetDateTime.now(ZoneOffset.UTC).minus(expirationInterval))
                .stream()
                .map(entity ->
                        new LinkDto(entity.getId(), entity.getUrl(), entity.getService(), entity.getLastUpdate()))
                .toList();
    }

    @Override
    @Transactional
    public void updateLink(long linkId, OffsetDateTime time) {
        LinkEntity linkEntity = linkRepository.findById(linkId).orElse(null);
        if (linkEntity == null) {
            throw new LinkNotTrackedException();
        }
        linkEntity.setLastUpdate(OffsetDateTime.now(ZoneOffset.UTC));
        linkRepository.save(linkEntity);
    }

    @Override
    @Transactional
    public List<Long> getUsersByLink(Long id) {
        return userRepository.findAllByLinks_Id(id).stream()
                .map(UserEntity::getId)
                .toList();
    }

    @Override
    @Transactional
    public void removeLink(String url) {
        LinkEntity link = linkRepository.findByUrl(url);
        if (link != null) {
            link.getUsers().forEach(user -> user.removeLink(link));
        }
    }
}
