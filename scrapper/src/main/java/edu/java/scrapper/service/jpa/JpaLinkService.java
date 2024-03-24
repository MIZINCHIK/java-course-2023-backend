package edu.java.scrapper.service.jpa;

import edu.java.model.dto.LinkResponse;
import edu.java.model.exceptions.MalformedUrlException;
import edu.java.model.links.Link;
import edu.java.scrapper.domain.jpa.LinkRepository;
import edu.java.scrapper.domain.jpa.UserRepository;
import edu.java.scrapper.domain.jpa.entities.LinkEntity;
import edu.java.scrapper.domain.jpa.entities.UserEntity;
import edu.java.scrapper.dto.LinkDto;
import edu.java.scrapper.exceptions.UserNotRegisteredException;
import edu.java.scrapper.service.ModifiableLinkStorage;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JpaLinkService implements ModifiableLinkStorage {
    private final LinkRepository linkRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public List<LinkResponse> getLinksByUserId(long userId) {
        return linkRepository.findAllByUsers_Id(userId).stream()
            .map(entity -> new LinkResponse(entity.getId(), entity.getUrl()))
            .toList();
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
        if (!linkRepository.existsByUsers_Id(userId)) {
            linkEntity.getUsers().add(userEntity);
        }
        return linkEntity.getId();
    }

    @Override
    @Transactional
    public long untrackLink(Link link, long userId) {
        return 0;
    }

    @Override
    @Transactional
    public boolean isLinkTracked(Link link, long userId) {
        return false;
    }

    @Override
    @Transactional
    public List<LinkDto> getLinksWithExpiredCheckTime(Duration expirationInterval) {
        return null;
    }

    @Override
    @Transactional
    public void updateLink(long linkId, OffsetDateTime time) {

    }

    @Override
    @Transactional
    public List<Long> getUsersByLink(Long id) {
        return null;
    }

    @Override
    @Transactional
    public void removeLink(String url) {

    }
}
