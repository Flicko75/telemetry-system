package com.telemetry.monitoring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telemetry.common.DTOs.AlertEntityDTO;
import com.telemetry.common.DTOs.AlertResponse;
import com.telemetry.common.enums.AlertStatus;
import com.telemetry.common.enums.SeverityLevel;
import com.telemetry.common.exceptions.ResourceNotFoundException;
import com.telemetry.monitoring.controllers.AlertController;
import com.telemetry.monitoring.exceptions.GlobalExceptionHandler;
import com.telemetry.monitoring.services.AlertService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlertController.class)
@ContextConfiguration(classes = {AlertController.class, GlobalExceptionHandler.class})
class AlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AlertService alertService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void getAlertsByDevice_returns200(){
        List<AlertResponse> alerts = new ArrayList<>();

        when(alertService.getAlertsByDevice("SAT-001")).thenReturn(alerts);

        mockMvc.perform(get("/api/v1/alerts/SAT-001"))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getAlertsByDevice_returns404(){
        when(alertService.getAlertsByDevice("SAT-001"))
                .thenThrow(new ResourceNotFoundException("Device Not Found"));

        mockMvc.perform(get("/api/v1/alerts/SAT-001"))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void resolveAlert_returns200(){
        AlertEntityDTO dto = new AlertEntityDTO(AlertStatus.RESOLVED);
        AlertResponse alertResponse = new AlertResponse(
                "SAT-001",
                SeverityLevel.CRITICAL,
                AlertStatus.RESOLVED,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(alertService.resolveAlert(1L, dto)).thenReturn(alertResponse);

        mockMvc.perform(patch("/api/v1/alerts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void resolveAlert_returns404(){
        AlertEntityDTO dto = new AlertEntityDTO(AlertStatus.RESOLVED);

        when(alertService.resolveAlert(1L, dto))
                .thenThrow(new ResourceNotFoundException("Alert Not Found"));

        mockMvc.perform(patch("/api/v1/alerts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

}
