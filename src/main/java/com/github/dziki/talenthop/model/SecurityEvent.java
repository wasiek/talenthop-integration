package com.github.dziki.talenthop.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "security_events")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_id", nullable = false)
    private IntegrationSource source;

    @Column(nullable = false)
    private String eventType; // e.g., "LOGIN_FAILED", "MALWARE_DETECTED", "DATA_EXFIL"

    @Column(nullable = false)
    private String severity; // e.g., "LOW", "MEDIUM", "HIGH", "CRITICAL"

    @Column(nullable = false, length = 4000)
    private String payload; // JSON payload from source

    @Column
    private String sourceIp;

    @Column
    private String destinationIp;

    @Column
    private String username;

    @Column(name = "is_processed")
    @Builder.Default
    private Boolean isProcessed = false;

    @Column(name = "processed_at")
    private Instant processedAt;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
