package edu.java.scrapper.service.jpa;

import edu.java.model.links.Link;
import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.jpa.LinkRepository;
import edu.java.scrapper.domain.jpa.UserRepository;
import edu.java.scrapper.exceptions.UserAlreadyRegisteredException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class JpaUserServiceTest extends IntegrationTest {
    private final JpaUserService userService;
    private final JpaLinkService linkService;

    @Autowired
    public JpaUserServiceTest(UserRepository userRepository, LinkRepository linkRepository) {
        userService = new JpaUserService(userRepository);
        linkService = new JpaLinkService(linkRepository, userRepository);
    }

    @RepeatedTest(5)
    @Transactional
    @Rollback
    @DisplayName("Add when new user")
    void registerUserANDisUserRegistered_whenNotRegistered_thenSuccess() {
        Random random = ThreadLocalRandom.current();
        long userId = random.nextLong();
        assertThat(userService.isUserRegistered(userId)).isFalse();
        assertDoesNotThrow(() -> userService.registerUser(userId));
        assertThat(userService.isUserRegistered(userId)).isTrue();
    }

    @RepeatedTest(5)
    @Transactional
    @Rollback
    @DisplayName("Add when duplicate user")
    void registerUserANDisUserRegistered_whenRegistered_thenUserAlreadyRegisteredException() {
        Random random = ThreadLocalRandom.current();
        long userId = random.nextLong();
        userService.registerUser(userId);
        assertThatThrownBy(() -> userService.registerUser(userId)).isInstanceOf(UserAlreadyRegisteredException.class);
    }

    @RepeatedTest(5)
    @Transactional
    @Rollback
    @DisplayName("Delete when user isn't registered")
    void deleteUserANDisUserRegistered_whenNotRegistered_thenSuccess() {
        Random random = ThreadLocalRandom.current();
        long userId = random.nextLong();
        assertThat(userService.isUserRegistered(userId)).isFalse();
        assertDoesNotThrow(() -> userService.deleteUser(userId));
        assertThat(userService.isUserRegistered(userId)).isFalse();
    }

    @RepeatedTest(5)
    @Transactional
    @Rollback
    @DisplayName("Delete when user is registered")
    void deleteUserANDisUserRegistered_whenRegistered_thenSuccess() {
        Random random = ThreadLocalRandom.current();
        long userId = random.nextLong();
        userService.registerUser(userId);
        assertThat(userService.isUserRegistered(userId)).isTrue();
        assertDoesNotThrow(() -> userService.deleteUser(userId));
        assertThat(userService.isUserRegistered(userId)).isFalse();
    }

    @RepeatedTest(5)
    @Transactional
    @Rollback
    @DisplayName("Delete when user is registered and tracks links")
    void deleteUser_whenRegisteredAndHasAssociatedLink_thenSuccess() {
        Random random = ThreadLocalRandom.current();
        long userId = random.nextLong();
        userService.registerUser(userId);
        long userId2 = random.nextLong();
        userService.registerUser(userId2);
        assertThat(userService.isUserRegistered(userId)).isTrue();
        Link link1 = new Link("https://github.com/23213");
        Link link2 = new Link("https://stackoverflow.com/12");
        linkService.trackLink(link1, userId);
        linkService.trackLink(link2, userId);
        linkService.trackLink(link1, userId2);
        assertDoesNotThrow(() -> userService.deleteUser(userId));
        assertThat(userService.isUserRegistered(userId)).isFalse();
        assertThat(linkService.isLinkTracked(link1, userId)).isFalse();
        assertThat(linkService.isLinkTracked(link2, userId)).isFalse();
    }
}
