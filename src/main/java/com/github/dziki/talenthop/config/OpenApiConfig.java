package com.github.dziki.talenthop.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI talentHopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TalentHop Integration Platform API")
                        .description("AI-powered security operations integration platform. " +
                                "Manages security data sources, processes security events, and provides transformation pipelines.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Dziki")
                                .email("dziki@example.com"))
                        .license(new License()
                                .name("MIT")));
    }
}
