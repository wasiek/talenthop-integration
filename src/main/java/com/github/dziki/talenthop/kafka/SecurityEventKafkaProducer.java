package com.github.dziki.talenthop.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityEventKafkaProducer {

    private static final String TOPIC = "security-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishEventProcessed(Long eventId, String eventType, String severity) {
        Map<String, Object> payload = Map.of(
                "eventId", eventId,
                "eventType", eventType,
                "severity", severity,
                "processedAt", Instant.now().toString()
        );

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    doSend(eventId, payload);
                }
            });
        } else {
            doSend(eventId, payload);
        }
    }

    private void doSend(Long eventId, Map<String, Object> payload) {
        kafkaTemplate.send(TOPIC, String.valueOf(eventId), payload);
        log.info("Published event-processed to Kafka: topic={}, eventId={}", TOPIC, eventId);
    }
}
