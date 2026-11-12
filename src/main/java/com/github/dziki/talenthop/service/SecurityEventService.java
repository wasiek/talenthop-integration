package com.github.dziki.talenthop.service;

import com.github.dziki.talenthop.dto.SecurityEventResponse;
import com.github.dziki.talenthop.model.IntegrationSource;
import com.github.dziki.talenthop.model.SecurityEvent;
import com.github.dziki.talenthop.repository.SecurityEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityEventService {

    private final SecurityEventRepository repository;

    @Transactional
    public SecurityEventResponse create(SecurityEvent event) {
        SecurityEvent saved = repository.save(event);
        log.debug("Created security event: {} ({})", saved.getEventType(), saved.getId());
        return SecurityEventResponse.fromEntity(saved);
    }

    public List<SecurityEventResponse> findAll() {
        return repository.findAll().stream()
                .map(SecurityEventResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<SecurityEventResponse> findBySourceId(Long sourceId) {
        return repository.findBySourceIdAndIsProcessedFalse(sourceId).stream()
                .map(SecurityEventResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<SecurityEventResponse> findBySeverity(String severity) {
        return repository.findBySeverityAndIsProcessedFalse(severity).stream()
                .map(SecurityEventResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<SecurityEventResponse> findUnprocessedSince(Instant since) {
        return repository.findUnprocessedSince(since).stream()
                .map(SecurityEventResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public long countBySourceId(Long sourceId) {
        return repository.countBySourceId(sourceId);
    }

    public long countBySeverity(String severity) {
        return repository.countBySeverity(severity);
    }

    @Transactional
    public void markAsProcessed(Long id) {
        SecurityEvent event = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + id));
        event.setIsProcessed(true);
        event.setProcessedAt(Instant.now());
        repository.save(event);
        log.debug("Marked event as processed: {}", id);
    }
}
