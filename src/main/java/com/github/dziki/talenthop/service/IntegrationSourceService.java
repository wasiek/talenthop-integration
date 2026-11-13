package com.github.dziki.talenthop.service;

import com.github.dziki.talenthop.dto.IntegrationSourceRequest;
import com.github.dziki.talenthop.dto.IntegrationSourceResponse;
import com.github.dziki.talenthop.exception.ResourceNotFoundException;
import com.github.dziki.talenthop.model.IntegrationSource;
import com.github.dziki.talenthop.repository.IntegrationSourceRepository;
import com.github.dziki.talenthop.repository.SecurityEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class IntegrationSourceService {

    private final IntegrationSourceRepository repository;
    private final SecurityEventRepository eventRepository;

    @Transactional
    public IntegrationSourceResponse create(IntegrationSourceRequest request) {
        if (repository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Source with name '" + request.getName() + "' already exists");
        }

        IntegrationSource source = IntegrationSource.builder()
                .name(request.getName())
                .type(request.getType())
                .endpoint(request.getEndpoint())
                .apiKey(request.getApiKey())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        IntegrationSource saved = repository.save(source);
        log.info("Created integration source: {} ({})", saved.getName(), saved.getId());
        return IntegrationSourceResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public Page<IntegrationSourceResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(IntegrationSourceResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public IntegrationSourceResponse findById(Long id) {
        IntegrationSource source = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Source", id));
        return IntegrationSourceResponse.fromEntity(source);
    }

    @Transactional(readOnly = true)
    public IntegrationSourceResponse findByName(String name) {
        IntegrationSource source = repository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Source not found: " + name));
        return IntegrationSourceResponse.fromEntity(source);
    }

    @Transactional
    public IntegrationSourceResponse update(Long id, IntegrationSourceRequest request) {
        IntegrationSource source = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Source", id));

        // Check for duplicate name (excluding current source)
        repository.findByName(request.getName())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Source with name '" + request.getName() + "' already exists");
                });

        source.setName(request.getName());
        source.setType(request.getType());
        source.setEndpoint(request.getEndpoint());
        source.setApiKey(request.getApiKey());
        if (request.getIsActive() != null) {
            source.setIsActive(request.getIsActive());
        }

        log.info("Updated integration source: {} ({})", source.getName(), source.getId());
        return IntegrationSourceResponse.fromEntity(source);
    }

    @Transactional
    public void delete(Long id) {
        IntegrationSource source = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Source", id));

        long eventCount = eventRepository.countBySourceId(id);
        if (eventCount > 0) {
            throw new IllegalStateException("Cannot delete source with " + eventCount + " associated events");
        }

        repository.delete(source);
        log.info("Deleted integration source: {} ({})", source.getName(), id);
    }
}
