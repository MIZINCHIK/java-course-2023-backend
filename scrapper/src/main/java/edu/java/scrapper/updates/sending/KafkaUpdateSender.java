package edu.java.scrapper.updates.sending;

import edu.java.model.dto.LinkUpdate;
import edu.java.model.kafka.TopicConfiguration;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.kafka", name = "use-queue", havingValue = "true")
@Log4j2
public class KafkaUpdateSender implements UpdateSender {
    private final KafkaTemplate<String, LinkUpdate> kafkaTemplate;
    @Value("#{kafkaConfiguration.topics.updates()}")
    private TopicConfiguration updates;
//    private final KafkaTemplate<String, String> badTemplate;

    public void sendUpdate(LinkUpdate update) {
        String message = update.toString();
//        badTemplate.send("updates", "error");
        CompletableFuture<SendResult<String, LinkUpdate>> future = kafkaTemplate.send(updates.name(), update);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent message=[{}] with offset=[{}]", message, result.getRecordMetadata().offset());
            } else {
                log.info("Unable to send message=[{}] due to : {}", message, ex.getMessage());
            }
        });
    }
}
