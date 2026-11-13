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
public class SecurityEventRequest {

    @NotNull(message = "Source ID is required")
    @Schema(example = "1")
    private Long sourceId;

    @NotBlank(message = "Event type is required")
    @Size(max = 100, message = "Event type must be at most 100 characters")
    @Schema(example = "LOGIN_FAILED")
    private String eventType;

    @NotBlank(message = "Severity is required")
    @Size(max = 20, message = "Severity must be at most 20 characters")
    @Schema(example = "CRITICAL")
    private String severity;

    @NotBlank(message = "Payload is required")
    @Size(max = 4000, message = "Payload must be at most 4000 characters")
    @Schema(example = "{\"user\": \"admin\", \"ip\": \"10.0.0.1\"}")
    private String payload;

    @Size(max = 45, message = "Source IP must be at most 45 characters")
    @Schema(example = "192.168.1.100")
    private String sourceIp;

    @Size(max = 45, message = "Destination IP must be at most 45 characters")
    @Schema(example = "10.0.0.5")
    private String destinationIp;

    @Size(max = 100, message = "Username must be at most 100 characters")
    @Schema(example = "admin")
    private String username;
}
