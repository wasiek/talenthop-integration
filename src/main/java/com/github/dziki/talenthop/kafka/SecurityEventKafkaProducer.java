package com.github.dziki.talenthop.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityEventKafkaProducer {

    private static final String TOPIC = "security-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishEventProcessed(Long eventId, String eventType, String severity) {
        var payload = java.util.Map.of(
                "eventId", eventId,
                "eventType", eventType,
                "severity", severity,
                "processedAt", java.time.Instant.now().toString()
        );
        kafkaTemplate.send(TOPIC, String.valueOf(eventId), payload);
        log.info("Published event-processed to Kafka: topic={}, eventId={}", TOPIC, eventId);
    }
}
