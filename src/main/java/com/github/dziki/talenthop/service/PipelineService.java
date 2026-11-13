package com.github.dziki.talenthop.service;

import com.github.dziki.talenthop.dto.PipelineRequest;
import com.github.dziki.talenthop.dto.PipelineResponse;
import com.github.dziki.talenthop.exception.ResourceNotFoundException;
import com.github.dziki.talenthop.model.IntegrationPipeline;
import com.github.dziki.talenthop.model.IntegrationSource;
import com.github.dziki.talenthop.repository.IntegrationPipelineRepository;
import com.github.dziki.talenthop.repository.IntegrationSourceRepository;
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
public class PipelineService {

    private final IntegrationPipelineRepository pipelineRepository;
    private final IntegrationSourceRepository sourceRepository;

    @Transactional
    public PipelineResponse create(PipelineRequest request) {
        if (pipelineRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Pipeline with name '" + request.getName() + "' already exists");
        }

        IntegrationSource source = sourceRepository.findById(request.getSourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Source", request.getSourceId()));

        IntegrationPipeline pipeline = IntegrationPipeline.builder()
                .name(request.getName())
                .source(source)
                .transformation(request.getTransformation())
                .build();

        IntegrationPipeline saved = pipelineRepository.save(pipeline);
        log.info("Created pipeline: {} ({})", saved.getName(), saved.getId());
        return PipelineResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public Page<PipelineResponse> findAll(Pageable pageable) {
        return pipelineRepository.findAll(pageable).map(PipelineResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public PipelineResponse findById(Long id) {
        IntegrationPipeline pipeline = pipelineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pipeline", id));
        return PipelineResponse.fromEntity(pipeline);
    }

    @Transactional
    public void delete(Long id) {
        IntegrationPipeline pipeline = pipelineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pipeline", id));
        pipelineRepository.delete(pipeline);
        log.info("Deleted pipeline: {} ({})", pipeline.getName(), id);
    }
}
