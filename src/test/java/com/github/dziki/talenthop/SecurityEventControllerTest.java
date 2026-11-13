package com.github.dziki.talenthop;

import com.github.dziki.talenthop.model.IntegrationSource;
import com.github.dziki.talenthop.model.SecurityEvent;
import com.github.dziki.talenthop.repository.IntegrationSourceRepository;
import com.github.dziki.talenthop.repository.SecurityEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SecurityEventRepository eventRepository;

    @Autowired
    private IntegrationSourceRepository sourceRepository;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    private IntegrationSource source;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();
        sourceRepository.deleteAll();

        source = IntegrationSource.builder()
                .name("TestSource")
                .type("SIEM")
                .endpoint("https://test.example.com")
                .build();
        source = sourceRepository.save(source);
    }

    @Test
    void ingestEvent_directSave() throws Exception {
        // Events are created via service (Kafka consumer), test via direct repository save
        SecurityEvent event = SecurityEvent.builder()
                .source(source)
                .eventType("MALWARE_DETECTED")
                .severity("CRITICAL")
                .payload("{\"file\": \"/tmp/malware.bin\"}")
                .build();
        event = eventRepository.save(event);

        mockMvc.perform(get("/api/v1/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].eventType").value("MALWARE_DETECTED"))
                .andExpect(jsonPath("$.content[0].severity").value("CRITICAL"))
                .andExpect(jsonPath("$.content[0].isProcessed").value(false));
    }

    @Test
    void findAllEvents() throws Exception {
        SecurityEvent event = SecurityEvent.builder()
                .source(source)
                .eventType("INTRUSION")
                .severity("HIGH")
                .payload("{\"ip\": \"1.2.3.4\"}")
                .build();
        eventRepository.save(event);

        mockMvc.perform(get("/api/v1/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].eventType").value("INTRUSION"))
                .andExpect(jsonPath("$.content[0].severity").value("HIGH"));
    }

    @Test
    void findBySource() throws Exception {
        SecurityEvent event = SecurityEvent.builder()
                .source(source)
                .eventType("PHISHING")
                .severity("MEDIUM")
                .payload("{\"email\": \"phish@evil.com\"}")
                .build();
        eventRepository.save(event);

        mockMvc.perform(get("/api/v1/events/source/" + source.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventType").value("PHISHING"));
    }

    @Test
    void findBySeverity() throws Exception {
        SecurityEvent critical = SecurityEvent.builder()
                .source(source)
                .eventType("RANSOMWARE")
                .severity("CRITICAL")
                .payload("{}")
                .build();
        SecurityEvent low = SecurityEvent.builder()
                .source(source)
                .eventType("INFO_LOG")
                .severity("LOW")
                .payload("{}")
                .build();
        eventRepository.save(critical);
        eventRepository.save(low);

        mockMvc.perform(get("/api/v1/events/severity/CRITICAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventType").value("RANSOMWARE"))
                .andExpect(jsonPath("$[0].severity").value("CRITICAL"));
    }

    @Test
    void findUnprocessedSince() throws Exception {
        SecurityEvent unprocessed = SecurityEvent.builder()
                .source(source)
                .eventType("NEW_THREAT")
                .severity("HIGH")
                .payload("{}")
                .build();
        SecurityEvent processed = SecurityEvent.builder()
                .source(source)
                .eventType("OLD_THREAT")
                .severity("LOW")
                .payload("{}")
                .isProcessed(true)
                .build();
        eventRepository.save(unprocessed);
        eventRepository.save(processed);

        // Use a past timestamp to get unprocessed events
        String since = java.time.Instant.now().minusSeconds(3600).toString();
        mockMvc.perform(get("/api/v1/events/unprocessed?since=" + since))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventType").value("NEW_THREAT"))
                .andExpect(jsonPath("$[0].isProcessed").value(false));
    }

    @Test
    void getStats() throws Exception {
        SecurityEvent e1 = SecurityEvent.builder()
                .source(source)
                .eventType("T1")
                .severity("CRITICAL")
                .payload("{}")
                .build();
        SecurityEvent e2 = SecurityEvent.builder()
                .source(source)
                .eventType("T2")
                .severity("LOW")
                .payload("{}")
                .build();
        eventRepository.save(e1);
        eventRepository.save(e2);

        mockMvc.perform(get("/api/v1/events/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEvents").value(2))
                .andExpect(jsonPath("$.criticalEvents").value(1))
                .andExpect(jsonPath("$.lowEvents").value(1));
    }

    @Test
    void markAsProcessed() throws Exception {
        SecurityEvent event = SecurityEvent.builder()
                .source(source)
                .eventType("TO_PROCESS")
                .severity("MEDIUM")
                .payload("{}")
                .build();
        event = eventRepository.save(event);

        mockMvc.perform(patch("/api/v1/events/" + event.getId() + "/process"))
                .andExpect(status().isNoContent());

        // Verify it's now processed
        mockMvc.perform(get("/api/v1/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].isProcessed").value(true));
    }

    @Test
    void markAsProcessed_notFound() throws Exception {
        mockMvc.perform(patch("/api/v1/events/9999/process"))
                .andExpect(status().isNotFound());
    }
}
