package com.github.dziki.talenthop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PipelineRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    @Schema(example = "siem-critical-filter")
    private String name;

    @NotNull(message = "Source ID is required")
    @Schema(example = "1")
    private Long sourceId;

    @NotBlank(message = "Transformation is required")
    @Schema(example = "{\"filter\": \"severity=CRITICAL\"}")
    private String transformation;
}
