package com.github.dziki.talenthop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntegrationSourceRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Type is required")
    private String type;

    @NotBlank(message = "Endpoint is required")
    private String endpoint;

    private String apiKey;

    private Boolean isActive;
}
