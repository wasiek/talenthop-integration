package com.github.dziki.talenthop.service;

import com.github.dziki.talenthop.dto.SecurityEventResponse;
import com.github.dziki.talenthop.exception.ResourceNotFoundException;
import com.github.dziki.talenthop.kafka.SecurityEventKafkaProducer;
import com.github.dziki.talenthop.model.IntegrationSource;
import com.github.dziki.talenthop.model.SecurityEvent;
import com.github.dziki.talenthop.repository.SecurityEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityEventService {

    private final SecurityEventRepository repository;
    private final SecurityEventKafkaProducer kafkaProducer;

    @Transactional
    public SecurityEventResponse create(SecurityEvent event) {
        SecurityEvent saved = repository.save(event);
        log.debug("Created security event: {} ({})", saved.getEventType(), saved.getId());
        return SecurityEventResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public Page<SecurityEventResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(SecurityEventResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<SecurityEventResponse> findBySourceId(Long sourceId) {
        return repository.findBySourceIdAndIsProcessedFalse(sourceId).stream()
                .map(SecurityEventResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SecurityEventResponse> findAllBySourceId(Long sourceId) {
        return repository.findBySourceId(sourceId).stream()
                .map(SecurityEventResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SecurityEventResponse> findBySeverity(String severity) {
        return repository.findBySeverityAndIsProcessedFalse(severity).stream()
                .map(SecurityEventResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SecurityEventResponse> findUnprocessedSince(Instant since) {
        return repository.findUnprocessedSince(since).stream()
                .map(SecurityEventResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long countAll() {
        return repository.count();
    }

    @Transactional(readOnly = true)
    public long countBySourceId(Long sourceId) {
        return repository.countBySourceId(sourceId);
    }

    @Transactional(readOnly = true)
    public long countBySeverity(String severity) {
        return repository.countBySeverity(severity);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> countBySeverityGrouped() {
        Map<String, Long> result = new java.util.HashMap<>();
        for (Object[] row : repository.countGroupBySeverity()) {
            result.put((String) row[0], (Long) row[1]);
        }
        return result;
    }

    @Transactional
    public void markAsProcessed(Long id) {
        SecurityEvent event = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", id));
        event.setIsProcessed(true);
        event.setProcessedAt(Instant.now());
        repository.save(event);
        log.debug("Marked event as processed: {}", id);

        kafkaProducer.publishEventProcessed(id, event.getEventType(), event.getSeverity());
    }
}
