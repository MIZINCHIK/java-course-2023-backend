package edu.java.bot.kafka_properties;

import io.micrometer.core.instrument.Counter;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageCountMetric {
    private final PrometheusMeterRegistry registry;
    private Counter counter;

    @Autowired
    public MessageCountMetric(PrometheusMeterRegistry registry) {
        this.registry = registry;
        counter = Counter.builder("processed_message_count")
            .baseUnit("msg amt")
            .tag("application", "bot")
            .description("Kafka Messages successfully processed by bot")
            .register(registry);
    }

    public void increment() {
        counter.increment();
    }
}
