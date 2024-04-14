package edu.java.scrapper.kafka_properties;

import edu.java.model.kafka.TopicConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.kafka", ignoreUnknownFields = false)
@Getter
@Setter
public class KafkaConfiguration {
    private String bootstrapServers;
    private boolean useQueue;
    private Topics topics;

    public record Topics(@NestedConfigurationProperty TopicConfiguration updates) {
    }
}
