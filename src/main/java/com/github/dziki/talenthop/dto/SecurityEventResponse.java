package com.github.dziki.talenthop.dto;

import com.github.dziki.talenthop.model.SecurityEvent;
import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityEventResponse {

    private Long id;
    private Long sourceId;
    private String sourceName;
    private String eventType;
    private String severity;
    private String payload;
    private String sourceIp;
    private String destinationIp;
    private String username;
    private Boolean isProcessed;
    private Instant createdAt;

    public static SecurityEventResponse fromEntity(SecurityEvent event) {
        return SecurityEventResponse.builder()
                .id(event.getId())
                .sourceId(event.getSource() != null ? event.getSource().getId() : null)
                .sourceName(event.getSource() != null ? event.getSource().getName() : null)
                .eventType(event.getEventType())
                .severity(event.getSeverity())
                .payload(event.getPayload())
                .sourceIp(event.getSourceIp())
                .destinationIp(event.getDestinationIp())
                .username(event.getUsername())
                .isProcessed(event.getIsProcessed())
                .createdAt(event.getCreatedAt())
                .build();
    }
}
