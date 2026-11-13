package com.github.dziki.talenthop.dto;

import com.github.dziki.talenthop.model.IntegrationPipeline;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PipelineResponse {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "siem-critical-filter")
    private String name;

    @Schema(example = "1")
    private Long sourceId;

    @Schema(example = "{\"filter\": \"severity=CRITICAL\"}")
    private String transformation;

    @Schema(example = "true")
    private Boolean isActive;

    @Schema(example = "2026-11-13T10:00:00Z")
    private Instant lastRunAt;

    @Schema(example = "SUCCESS")
    private String lastStatus;

    @Schema(example = "2026-11-13T09:00:00Z")
    private Instant createdAt;

    public static PipelineResponse fromEntity(IntegrationPipeline pipeline) {
        return PipelineResponse.builder()
                .id(pipeline.getId())
                .name(pipeline.getName())
                .sourceId(pipeline.getSource() != null ? pipeline.getSource().getId() : null)
                .transformation(pipeline.getTransformation())
                .isActive(pipeline.getIsActive())
                .lastRunAt(pipeline.getLastRunAt())
                .lastStatus(pipeline.getLastStatus())
                .createdAt(pipeline.getCreatedAt())
                .build();
    }
}
