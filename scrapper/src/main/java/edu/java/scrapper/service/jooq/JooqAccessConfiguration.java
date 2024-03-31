package edu.java.scrapper.service.jooq;

import edu.java.model.storage.UserStorage;
import edu.java.scrapper.service.ModifiableLinkStorage;
import org.jooq.DSLContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jooq")
public class JooqAccessConfiguration {
    @Bean
    public ModifiableLinkStorage linkService(
        DSLContext dslContext
    ) {
        return new JooqLinkService(dslContext);
    }

    @Bean
    public UserStorage userService(
        DSLContext dslContext
    ) {
        return new JooqUserService(dslContext);
    }
}
