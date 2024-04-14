package edu.java.scrapper.updates.sending;

import edu.java.model.dto.LinkUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.kafka", name = "use-queue", havingValue = "true")
public class KafkaUpdateSender implements UpdateSender {
    private final KafkaTemplate<String, LinkUpdate> kafkaTemplate;
    private final KafkaTemplate<String, String> badTemplate;

    public void sendUpdate(LinkUpdate update) {
        String message = update.toString();
        badTemplate.send("updates", "error");
//        CompletableFuture<SendResult<String, LinkUpdate>> future = kafkaTemplate.send("updates", update);
//        future.whenComplete((result, ex) -> {
//            if (ex == null) {
//                System.out.println("Sent message=[" + message +
//                    "] with offset=[" + result.getRecordMetadata().offset() + "]");
//            } else {
//                System.out.println("Unable to send message=[" +
//                    message + "] due to : " + ex.getMessage());
//            }
//        });
    }
}
