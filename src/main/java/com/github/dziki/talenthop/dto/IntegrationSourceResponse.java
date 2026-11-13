package com.github.dziki.talenthop.dto;

import com.github.dziki.talenthop.model.IntegrationSource;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntegrationSourceResponse {

    private Long id;
    private String name;
    private String type;
    private String endpoint;
    private Boolean isActive;
    private Instant lastSyncAt;
    private Instant createdAt;
    private Instant updatedAt;

    public static IntegrationSourceResponse fromEntity(IntegrationSource source) {
        return IntegrationSourceResponse.builder()
                .id(source.getId())
                .name(source.getName())
                .type(source.getType())
                .endpoint(source.getEndpoint())
                .isActive(source.getIsActive())
                .lastSyncAt(source.getLastSyncAt())
                .createdAt(source.getCreatedAt())
                .updatedAt(source.getUpdatedAt())
                .build();
    }
}
