package com.github.dziki.talenthop;

import com.github.dziki.talenthop.model.IntegrationPipeline;
import com.github.dziki.talenthop.model.IntegrationSource;
import com.github.dziki.talenthop.repository.IntegrationPipelineRepository;
import com.github.dziki.talenthop.repository.IntegrationSourceRepository;
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
class PipelineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IntegrationPipelineRepository pipelineRepository;

    @Autowired
    private IntegrationSourceRepository sourceRepository;

    private IntegrationSource source;

    @BeforeEach
    void setUp() {
        pipelineRepository.deleteAll();
        sourceRepository.deleteAll();

        source = IntegrationSource.builder()
                .name("PipeSource")
                .type("SIEM")
                .endpoint("https://pipe.example.com")
                .build();
        source = sourceRepository.save(source);
    }

    @Test
    void createPipeline() throws Exception {
        String json = """
                {
                    "name": "test-pipeline",
                    "sourceId": %d,
                    "transformation": "test-transform"
                }
                """.formatted(source.getId());

        mockMvc.perform(post("/api/v1/pipelines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("test-pipeline"));
    }

    @Test
    void createPipeline_duplicate() throws Exception {
        IntegrationPipeline pipeline = IntegrationPipeline.builder()
                .name("dup-pipeline")
                .source(source)
                .transformation("{}")
                .build();
        pipelineRepository.save(pipeline);

        String json = """
                {
                    "name": "dup-pipeline",
                    "sourceId": %d,
                    "transformation": "{}"
                }
                """.formatted(source.getId());

        mockMvc.perform(post("/api/v1/pipelines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void findAllPipelines() throws Exception {
        IntegrationPipeline pipeline = IntegrationPipeline.builder()
                .name("find-me")
                .source(source)
                .transformation("{}")
                .build();
        pipelineRepository.save(pipeline);

        mockMvc.perform(get("/api/v1/pipelines"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("find-me"));
    }

    @Test
    void findPipelineById() throws Exception {
        IntegrationPipeline pipeline = IntegrationPipeline.builder()
                .name("get-me")
                .source(source)
                .transformation("{}")
                .build();
        pipeline = pipelineRepository.save(pipeline);

        mockMvc.perform(get("/api/v1/pipelines/" + pipeline.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("get-me"));
    }

    @Test
    void findPipelineById_notFound() throws Exception {
        mockMvc.perform(get("/api/v1/pipelines/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Pipeline not found: 999"));
    }

    @Test
    void deletePipeline() throws Exception {
        IntegrationPipeline pipeline = IntegrationPipeline.builder()
                .name("delete-me")
                .source(source)
                .transformation("{}")
                .build();
        pipeline = pipelineRepository.save(pipeline);

        mockMvc.perform(delete("/api/v1/pipelines/" + pipeline.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/pipelines/" + pipeline.getId()))
                .andExpect(status().isNotFound());
    }
}
