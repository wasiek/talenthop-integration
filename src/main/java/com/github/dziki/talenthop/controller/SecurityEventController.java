package com.github.dziki.talenthop.controller;

import com.github.dziki.talenthop.dto.SecurityEventResponse;
import com.github.dziki.talenthop.service.SecurityEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class SecurityEventController {

    private final SecurityEventService service;

    @GetMapping
    public ResponseEntity<Page<SecurityEventResponse>> findAll(
            @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/source/{sourceId}")
    public ResponseEntity<List<SecurityEventResponse>> findBySource(
            @PathVariable Long sourceId,
            @RequestParam(defaultValue = "false") boolean includeProcessed) {
        if (includeProcessed) {
            return ResponseEntity.ok(service.findAllBySourceId(sourceId));
        }
        return ResponseEntity.ok(service.findBySourceId(sourceId));
    }

    @GetMapping("/severity/{severity}")
    public ResponseEntity<List<SecurityEventResponse>> findBySeverity(@PathVariable String severity) {
        return ResponseEntity.ok(service.findBySeverity(severity));
    }

    @GetMapping("/unprocessed")
    public ResponseEntity<List<SecurityEventResponse>> findUnprocessedSince(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant since) {
        return ResponseEntity.ok(service.findUnprocessedSince(since));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Long> bySeverity = service.countBySeverityGrouped();
        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalEvents", service.countAll());
        stats.put("criticalEvents", bySeverity.getOrDefault("CRITICAL", 0L));
        stats.put("highEvents", bySeverity.getOrDefault("HIGH", 0L));
        stats.put("mediumEvents", bySeverity.getOrDefault("MEDIUM", 0L));
        stats.put("lowEvents", bySeverity.getOrDefault("LOW", 0L));
        return ResponseEntity.ok(stats);
    }

    @PatchMapping("/{id}/process")
    public ResponseEntity<Void> markAsProcessed(@PathVariable Long id) {
        service.markAsProcessed(id);
        return ResponseEntity.noContent().build();
    }
}
