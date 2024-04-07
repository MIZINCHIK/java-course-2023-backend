package edu.java.bot.configuration;

import edu.java.bot.service.LinkUpdateService;
import edu.java.model.dto.LinkUpdate;
import edu.java.model.kafka.TopicConfiguration;
import edu.java.model.kafka.serdes.UpdateDeserializer;
import edu.java.model.kafka.serdes.UpdateSerializer;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;

@Log4j2
@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaConfiguration {
    private final LinkUpdateService service;
    @Value(value = "#{@kafka.bootstrapServers()}")
    private String bootstrapAddress;
    @Value(value = "#{@kafka.topics().updates()}")
    private TopicConfiguration updates;
    @Value(value = "#{@kafka.topics().dlq()}")
    private TopicConfiguration dlq;

    @Bean
    public ConsumerFactory<String, LinkUpdate> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress,
            ConsumerConfig.GROUP_ID_CONFIG, updates.name(),
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class,
            ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, UpdateDeserializer.class.getName()
        ));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, LinkUpdate>
    kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, LinkUpdate> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        var errorHandler = new DefaultErrorHandler(new DeadLetterPublishingRecoverer(
            kafkaTemplate(), (ignored1, ignored2) -> new TopicPartition(dlq.name(), -1)));
        errorHandler.addNotRetryableExceptions(Exception.class);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    @KafkaListener(topics = "${app.scrapper-topic.name}")
    public void listen(LinkUpdate update) {
        service.processUpdate(update);
        log.info("Update processed: {}", update);
    }

    @Bean
    public NewTopic topicDlq() {
        return TopicBuilder.name("updates_dlq")
            .partitions(dlq.partitions())
            .replicas(dlq.replicas())
            .build();
    }

    @Bean
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
}
