// KafkaEventPublisher.java
package com.asid.order_saga_orchestrator.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publish(String topic, String key, String payload) {
        kafkaTemplate.send(topic, key, payload);
    }
}
