package edu.java.scrapper.service.jpa;

import edu.java.model.storage.UserStorage;
import edu.java.scrapper.domain.jpa.LinkRepository;
import edu.java.scrapper.domain.jpa.UserRepository;
import edu.java.scrapper.service.ModifiableLinkStorage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public class JpaAccessConfiguration {
    @Bean
    public ModifiableLinkStorage linkService(
        LinkRepository linkRepository,
        UserRepository userRepository
    ) {
        return new JpaLinkService(linkRepository, userRepository);
    }

    @Bean
    public UserStorage userService(
        UserRepository userRepository
    ) {
        return new JpaUserService(userRepository);
    }
}
