package edu.java.model.kafka;

import jakarta.validation.constraints.NotEmpty;

public record TopicConfiguration(@NotEmpty String name, Integer replicas, Integer partitions) {
}
