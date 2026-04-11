package com.telemetry.ingestion;

import com.telemetry.common.enums.DeviceHealth;
import com.telemetry.common.models.Coordinates;
import com.telemetry.common.models.TelemetryPacket;
import com.telemetry.ingestion.controllers.IngestionController;
import com.telemetry.ingestion.services.IngestionService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IngestionController.class)
@ExtendWith(MockitoExtension.class)
class IngestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IngestionService ingestionService;

    @Autowired
    private ObjectMapper objectMapper;

    private TelemetryPacket buildPacket(String deviceId, Map<String, Object> measurements,
                                        DeviceHealth deviceHealth, LocalDateTime sendingTime){
        TelemetryPacket packet = new TelemetryPacket();
        packet.setDeviceId(deviceId);
        packet.setMeasurements(measurements);
        packet.setDeviceHealth(deviceHealth);
        packet.setSendingTime(sendingTime);
        packet.setCoordinates(new Coordinates(40.7128, -74.0060));
        return packet;
    }

    @Test
    @SneakyThrows
    void receivePacket_return202() {
        TelemetryPacket packet = buildPacket(
                "SAT-001",
                Map.of("battery", 90, "temperature", 35),
                DeviceHealth.HEALTHY,
                LocalDateTime.now()
        );

        when(ingestionService.receivePacket(any(TelemetryPacket.class)))
                .thenReturn(ResponseEntity.accepted().body("Packet successfully received"));

        mockMvc.perform(post("/api/v1/telemetry")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(packet)))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Packet successfully received"));
    }

    @Test
    @SneakyThrows
    void receivePacket_deviceIdNull_returns400(){
        TelemetryPacket packet = buildPacket(
                null,
                Map.of("battery", 90, "temperature", 35),
                DeviceHealth.HEALTHY,
                LocalDateTime.now()
        );

        mockMvc.perform(post("/api/v1/telemetry")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(packet)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void receivePacket_measurementsNull_return400(){
        TelemetryPacket packet = buildPacket(
                "SAT-001",
                Map.of(),
                DeviceHealth.HEALTHY,
                LocalDateTime.now()
        );

        mockMvc.perform(post("/api/v1/telemetry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(packet)))
                .andExpect(status().isBadRequest());
    }

}
