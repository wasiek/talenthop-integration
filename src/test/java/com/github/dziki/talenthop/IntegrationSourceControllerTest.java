package com.github.dziki.talenthop;

import com.github.dziki.talenthop.dto.IntegrationSourceRequest;
import com.github.dziki.talenthop.model.IntegrationSource;
import com.github.dziki.talenthop.repository.IntegrationSourceRepository;
import com.github.dziki.talenthop.repository.SecurityEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class IntegrationSourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IntegrationSourceRepository sourceRepository;

    @Autowired
    private SecurityEventRepository eventRepository;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();
        sourceRepository.deleteAll();
    }

    @Test
    void createSource_success() throws Exception {
        IntegrationSourceRequest request = IntegrationSourceRequest.builder()
                .name("Test SIEM")
                .type("SIEM")
                .endpoint("https://siem.example.com")
                .apiKey("test-key")
                .build();

        mockMvc.perform(post("/api/v1/sources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(com.fasterxml.jackson.databind.json.JsonMapper.builder().build().writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test SIEM"))
                .andExpect(jsonPath("$.type").value("SIEM"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void createSource_duplicateName() throws Exception {
        IntegrationSource source = IntegrationSource.builder()
                .name("Duplicate")
                .type("SIEM")
                .endpoint("https://dup.example.com")
                .build();
        sourceRepository.save(source);

        IntegrationSourceRequest request = IntegrationSourceRequest.builder()
                .name("Duplicate")
                .type("SIEM")
                .endpoint("https://dup2.example.com")
                .build();

        mockMvc.perform(post("/api/v1/sources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(com.fasterxml.jackson.databind.json.JsonMapper.builder().build().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void createSource_validationError() throws Exception {
        String json = "{\"name\": \"\"}";

        mockMvc.perform(post("/api/v1/sources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void getAllSources() throws Exception {
        IntegrationSource source = IntegrationSource.builder()
                .name("Source1")
                .type("FIREWALL")
                .endpoint("https://fw.example.com")
                .build();
        sourceRepository.save(source);

        mockMvc.perform(get("/api/v1/sources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Source1"))
                .andExpect(jsonPath("$[0].type").value("FIREWALL"));
    }

    @Test
    void getSourceById_notFound() throws Exception {
        mockMvc.perform(get("/api/v1/sources/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Source not found: 999"));
    }

    @Test
    void updateSource() throws Exception {
        IntegrationSource source = IntegrationSource.builder()
                .name("OldName")
                .type("SIEM")
                .endpoint("https://old.example.com")
                .build();
        source = sourceRepository.save(source);

        IntegrationSourceRequest request = IntegrationSourceRequest.builder()
                .name("NewName")
                .type("FIREWALL")
                .endpoint("https://new.example.com")
                .apiKey("new-key")
                .build();

        mockMvc.perform(put("/api/v1/sources/" + source.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(com.fasterxml.jackson.databind.json.JsonMapper.builder().build().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NewName"))
                .andExpect(jsonPath("$.type").value("FIREWALL"))
                .andExpect(jsonPath("$.endpoint").value("https://new.example.com"));
    }

    @Test
    void updateSource_notFound() throws Exception {
        IntegrationSourceRequest request = IntegrationSourceRequest.builder()
                .name("NewName")
                .type("SIEM")
                .endpoint("https://new.example.com")
                .build();

        mockMvc.perform(put("/api/v1/sources/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(com.fasterxml.jackson.databind.json.JsonMapper.builder().build().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Source not found: 999"));
    }

    @Test
    void findSourceByName() throws Exception {
        IntegrationSource source = IntegrationSource.builder()
                .name("UniqueName")
                .type("ENDPOINT")
                .endpoint("https://ep.example.com")
                .build();
        sourceRepository.save(source);

        mockMvc.perform(get("/api/v1/sources/name/UniqueName"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UniqueName"))
                .andExpect(jsonPath("$.type").value("ENDPOINT"));
    }

    @Test
    void findSourceByName_notFound() throws Exception {
        mockMvc.perform(get("/api/v1/sources/name/NonExistent"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Source not found: NonExistent"));
    }

    @Test
    void deleteSource() throws Exception {
        IntegrationSource source = IntegrationSource.builder()
                .name("ToDelete")
                .type("CLOUD")
                .endpoint("https://cloud.example.com")
                .build();
        source = sourceRepository.save(source);

        mockMvc.perform(delete("/api/v1/sources/" + source.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/sources/" + source.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteSource_notFound() throws Exception {
        mockMvc.perform(delete("/api/v1/sources/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Source not found: 999"));
    }
}
