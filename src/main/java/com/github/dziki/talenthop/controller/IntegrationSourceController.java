package com.github.dziki.talenthop.controller;

import com.github.dziki.talenthop.dto.IntegrationSourceRequest;
import com.github.dziki.talenthop.dto.IntegrationSourceResponse;
import com.github.dziki.talenthop.service.IntegrationSourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sources")
@RequiredArgsConstructor
public class IntegrationSourceController {

    private final IntegrationSourceService service;

    @PostMapping
    public ResponseEntity<IntegrationSourceResponse> create(@Valid @RequestBody IntegrationSourceRequest request) {
        IntegrationSourceResponse response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<IntegrationSourceResponse>> findAll(
            @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IntegrationSourceResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<IntegrationSourceResponse> findByName(@PathVariable String name) {
        return ResponseEntity.ok(service.findByName(name));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IntegrationSourceResponse> update(@PathVariable Long id,
                                                            @Valid @RequestBody IntegrationSourceRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
