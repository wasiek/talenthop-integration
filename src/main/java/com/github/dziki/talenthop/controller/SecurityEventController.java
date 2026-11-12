package com.github.dziki.talenthop.controller;

import com.github.dziki.talenthop.dto.SecurityEventResponse;
import com.github.dziki.talenthop.service.SecurityEventService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<SecurityEventResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/source/{sourceId}")
    public ResponseEntity<List<SecurityEventResponse>> findBySource(@PathVariable Long sourceId) {
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
        return ResponseEntity.ok(Map.of(
                "totalEvents", service.findAll().size(),
                "criticalEvents", service.countBySeverity("CRITICAL"),
                "highEvents", service.countBySeverity("HIGH"),
                "mediumEvents", service.countBySeverity("MEDIUM"),
                "lowEvents", service.countBySeverity("LOW")
        ));
    }

    @PatchMapping("/{id}/process")
    public ResponseEntity<Void> markAsProcessed(@PathVariable Long id) {
        service.markAsProcessed(id);
        return ResponseEntity.noContent().build();
    }
}
