package com.github.dziki.talenthop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntegrationSourceRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @NotBlank(message = "Type is required")
    @Size(max = 20, message = "Type must be at most 20 characters")
    private String type;

    @NotBlank(message = "Endpoint is required")
    @Size(max = 500, message = "Endpoint must be at most 500 characters")
    private String endpoint;

    @Size(max = 255, message = "API key must be at most 255 characters")
    private String apiKey;

    private Boolean isActive;
}
