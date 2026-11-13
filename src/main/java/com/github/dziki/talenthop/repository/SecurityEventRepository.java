package com.github.dziki.talenthop.repository;

import com.github.dziki.talenthop.model.SecurityEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface SecurityEventRepository extends JpaRepository<SecurityEvent, Long> {

    List<SecurityEvent> findBySourceId(Long sourceId);
    List<SecurityEvent> findBySourceIdAndIsProcessedFalse(Long sourceId);

    List<SecurityEvent> findBySeverityAndIsProcessedFalse(String severity);

    @Query("SELECT e FROM SecurityEvent e WHERE e.createdAt >= :since AND e.isProcessed = false")
    List<SecurityEvent> findUnprocessedSince(@Param("since") Instant since);

    long countBySourceId(Long sourceId);

    long countBySeverity(String severity);
}
