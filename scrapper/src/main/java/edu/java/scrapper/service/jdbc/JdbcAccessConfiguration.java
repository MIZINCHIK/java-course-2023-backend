package edu.java.scrapper.service.jdbc;

import edu.java.model.storage.UserStorage;
import edu.java.scrapper.domain.jdbc.JdbcFollowingLinksDao;
import edu.java.scrapper.domain.jdbc.JdbcLinksDao;
import edu.java.scrapper.domain.jdbc.JdbcUsersDao;
import edu.java.scrapper.service.ModifiableLinkStorage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcAccessConfiguration {
    @Bean
    public ModifiableLinkStorage linkService(
        JdbcLinksDao linksDao,
        JdbcFollowingLinksDao followingLinksDao
    ) {
        return new JdbcLinkService(linksDao, followingLinksDao);
    }

    @Bean
    public UserStorage userService(
        JdbcUsersDao usersDao
    ) {
        return new JdbcUserService(usersDao);
    }
}
