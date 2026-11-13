package com.github.dziki.talenthop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "integration_pipelines")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties("hibernateLazyInitializer")
public class IntegrationPipeline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_id", nullable = false)
    private IntegrationSource source;

    @JsonIgnore
    public IntegrationSource getSource() {
        return source;
    }

    @Column(nullable = false)
    private String transformation;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "last_run_at")
    private Instant lastRunAt;

    @Column(name = "last_status")
    private String lastStatus;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
