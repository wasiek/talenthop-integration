package com.github.dziki.talenthop.controller;

import com.github.dziki.talenthop.model.IntegrationPipeline;
import com.github.dziki.talenthop.model.IntegrationSource;
import com.github.dziki.talenthop.repository.IntegrationPipelineRepository;
import com.github.dziki.talenthop.repository.IntegrationSourceRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pipelines")
@RequiredArgsConstructor
public class PipelineController {

    private final IntegrationPipelineRepository pipelineRepository;
    private final IntegrationSourceRepository sourceRepository;

    @Data
    public static class PipelineRequest {
        private String name;
        private Long sourceId;
        private String transformation;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<IntegrationPipeline> create(@RequestBody PipelineRequest request) {
        if (pipelineRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Pipeline with name '" + request.getName() + "' already exists");
        }
        IntegrationSource source = sourceRepository.findById(request.getSourceId())
                .orElseThrow(() -> new IllegalArgumentException("Source not found: " + request.getSourceId()));

        IntegrationPipeline pipeline = IntegrationPipeline.builder()
                .name(request.getName())
                .source(source)
                .transformation(request.getTransformation())
                .build();

        IntegrationPipeline saved = pipelineRepository.save(pipeline);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<IntegrationPipeline>> findAll() {
        return ResponseEntity.ok(pipelineRepository.findAll());
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<IntegrationPipeline> findById(@PathVariable Long id) {
        IntegrationPipeline pipeline = pipelineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pipeline not found: " + id));
        return ResponseEntity.ok(pipeline);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        IntegrationPipeline pipeline = pipelineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pipeline not found: " + id));
        pipelineRepository.delete(pipeline);
        return ResponseEntity.noContent().build();
    }
}
