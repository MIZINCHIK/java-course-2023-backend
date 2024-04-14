package edu.java.scrapper.kafka_properties;

import edu.java.model.dto.LinkUpdate;
import edu.java.model.kafka.TopicConfiguration;
import edu.java.model.kafka.serdes.UpdateSerializer;
import java.util.HashMap;
import java.util.Map;
import edu.java.scrapper.updates.sending.UpdateSender;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@ConditionalOnProperty(prefix = "spring.kafka", name = "use-queue", havingValue = "true")
public class KafkaBeans {
    @Value(value = "#{@kafkaConfiguration.bootstrapServers}")
    private String bootstrapAddress;
    @Value(value = "#{@kafkaConfiguration.topics.updates()}")
    private TopicConfiguration updates;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(updates.name())
            .partitions(updates.partitions())
            .replicas(updates.replicas())
            .build();
    }

    public ProducerFactory<String, LinkUpdate> producerFactory() {
        return new DefaultKafkaProducerFactory<>(Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, UpdateSerializer.class
        ));
    }

    @Bean
    public KafkaTemplate<String, LinkUpdate> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public KafkaTemplate<String, String> badTemplate() {
        return new KafkaTemplate<>(
            new DefaultKafkaProducerFactory<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class
            ))
        );
    }
}
