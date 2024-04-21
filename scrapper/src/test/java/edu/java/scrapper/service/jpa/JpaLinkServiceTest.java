package edu.java.scrapper.service.jpa;

import edu.java.model.dto.LinkResponse;
import edu.java.model.links.Link;
import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.jpa.LinkRepository;
import edu.java.scrapper.domain.jpa.UserRepository;
import edu.java.scrapper.dto.LinkDto;
import edu.java.scrapper.exceptions.LinkNotTrackedException;
import edu.java.scrapper.exceptions.UserNotRegisteredException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class JpaLinkServiceTest extends IntegrationTest {
    private final JpaLinkService linkService;
    private final JpaUserService userService;

    @Autowired
    public JpaLinkServiceTest(LinkRepository linkRepository, UserRepository userRepository) {
        linkService = new JpaLinkService(linkRepository, userRepository);
        userService = new JpaUserService(userRepository);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Get links when user doesn't have any")
    void getLinksByUserId_whenNoLinks_thenEmptyList() {
        assertThat(linkService.getLinksByUserId(0L).size()).isEqualTo(0);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Get links when user does have some tracked ones")
    void getLinksByUserIdANDtrackLink_whenThereAreSomeLinks_thenFullList() {
        List<Link> urls = List.of(
            new Link("https://github.com/asdsadad"),
            new Link("https://github.com/asdsadadsadsad"),
            new Link("https://github.com/er4g4g45g"),
            new Link("https://www.stackoverflow.com/sasdsad")
        );
        userService.registerUser(0L);
        for (var url : urls) {
            linkService.trackLink(url, 0L);
        }
        assertThat(linkService.getLinksByUserId(0L).size()).isEqualTo(4);
        assertThat(linkService.getLinksByUserId(0L).stream()
            .map(link -> new LinkResponse(0L, link.url()))
            .toList()).isEqualTo(
            urls.stream()
                .map(link -> {
                    try {
                        return new LinkResponse(0L, link.getUrl().toURI());
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList()
        );
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Track link when user isn't registered")
    void trackLink_whenUserIsNotReigstered_thenUserNotRegisteredException() {
        assertThatThrownBy(() -> linkService.trackLink(new Link("https://www.github.com/asdsad"), 0L)).isInstanceOf(
            UserNotRegisteredException.class);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Track link when it hasn't been tracked yet")
    void trackLinkANDisLinkTracked_whenHasNotBeenTracked_thenSuccess() {
        Link link = new Link("https://www.github.com/asdsad");
        assertThat(linkService.isLinkTracked(link, 0L)).isFalse();
        userService.registerUser(0L);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Track link when it has been tracked")
    void trackLinkANDisLinkTracked_whenHasBeenTracked_thenSuccess() {
        Link link = new Link("https://www.github.com/asdsad");
        userService.registerUser(0L);
        linkService.trackLink(link, 0L);
        assertThat(linkService.isLinkTracked(link, 0L)).isTrue();
        assertDoesNotThrow(() -> linkService.trackLink(link, 0L));
        assertThat(linkService.isLinkTracked(link, 0L)).isTrue();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Untrack link when it hasn't been tracked yet")
    void untrackLinkANDisLinkTracked_whenHasBeenTracked_thenLinkNotTrackedException() {
        Link link = new Link("https://www.github.com/asdsad");
        userService.registerUser(0L);
        linkService.trackLink(link, 0L);
        assertThat(linkService.isLinkTracked(link, 0L)).isTrue();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Get expired links")
    void getLinksWithExpiredCheckTime_whenExampleData_thenSuccess() {
        assertThat(linkService.getLinksWithExpiredCheckTime(Duration.of(3L, ChronoUnit.DAYS)).size()).isEqualTo(0);
        List<Link> urls = List.of(
            new Link("https://github.com/asdsadad"),
            new Link("https://github.com/asdsadadsadsad"),
            new Link("https://github.com/er4g4g45g"),
            new Link("https://www.stackoverflow.com/sasdsad")
        );
        userService.registerUser(0L);
        for (var link : urls) {
            linkService.trackLink(link, 0L);
        }
        assertThat(linkService.getLinksWithExpiredCheckTime(Duration.of(2, ChronoUnit.DAYS)).size()).isEqualTo(0);
        assertThat(linkService.getLinksWithExpiredCheckTime(Duration.ZERO)
            .stream().map(dto -> new LinkDto(0L, dto.url(), dto.service(), null)).toList()).isEqualTo(
            urls.stream().map(link -> new LinkDto(0L, link.getUrl().toString(), link.getDomain(), null)).toList()
        );
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Update link when it's not in the db")
    void updateLink_whenHasNotBeenTracked_thenLinkNotTrackedException() {
        assertThatThrownBy(() -> linkService.updateLink(0L, OffsetDateTime.now()))
            .isInstanceOf(LinkNotTrackedException.class);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Update link when it is in the db")
    void updateLink_whenHasBeenTracked_thenSuccess() {
        userService.registerUser(0L);
        Link link = new Link("https://github.com/asdsadad");
        Long linkId = linkService.trackLink(link, 0L);
        assertThat(linkService.isLinkTracked(link, 0L)).isTrue();
        assertDoesNotThrow(() -> linkService.updateLink(linkId, OffsetDateTime.now()));
        assertThat(linkService.isLinkTracked(link, 0L)).isTrue();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Get users by link id")
    void getUsersByLink_whenSomeData_thenSuccess() {
        userService.registerUser(0L);
        userService.registerUser(1L);
        List<Link> urls = List.of(
            new Link("https://github.com/asdsadad"),
            new Link("https://github.com/asdsadadsadsad")
        );
        assertThat(linkService.getUsersByLink(0L).size()).isEqualTo(0);
        Long firstId = linkService.trackLink(urls.getFirst(), 0L);
        linkService.trackLink(urls.getFirst(), 1L);
        Long secondId = linkService.trackLink(urls.getLast(), 0L);
        assertThat(linkService.getUsersByLink(firstId)).isEqualTo(List.of(0L, 1L));
        assertThat(linkService.getUsersByLink(secondId)).isEqualTo(List.of(0L));
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Remove link when it wasn't added")
    void removeLink_whenIsNotThere_thenSuccess() {
        assertDoesNotThrow(() -> linkService.removeLink("https://github.com/asdsadad"));
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Remove link when it was added before")
    void removeLink_whenIsThere_thenSuccess() {
        userService.registerUser(0L);
        Link link = new Link("https://github.com/asdsadad");
        linkService.trackLink(link, 0L);
        assertThat(linkService.isLinkTracked(link, 0L)).isTrue();
        linkService.removeLink(link.getUrl().toString());
        assertThat(linkService.isLinkTracked(link, 0L)).isFalse();
    }
}
