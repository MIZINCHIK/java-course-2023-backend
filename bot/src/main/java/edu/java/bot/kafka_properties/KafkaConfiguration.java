package edu.java.bot.kafka_properties;

import edu.java.model.kafka.TopicConfiguration;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Log4j2
@EnableKafka
@Configuration
@ConfigurationProperties(prefix = "spring.kafka", ignoreUnknownFields = false)
@Getter
@Setter
public class KafkaConfiguration {
    private String bootstrapServers;
    private Topics topics;
    private boolean useQueue;

    public record Topics(@NestedConfigurationProperty TopicConfiguration updates,
                          @NestedConfigurationProperty TopicConfiguration dlq) {
    }
}
