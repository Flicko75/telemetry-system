package com.telemetry.monitoring;

import com.telemetry.common.DTOs.TelemetryPacketResponse;
import com.telemetry.common.exceptions.ResourceNotFoundException;
import com.telemetry.monitoring.controllers.TelemetryController;
import com.telemetry.monitoring.exceptions.GlobalExceptionHandler;
import com.telemetry.monitoring.services.TelemetryQueryService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TelemetryController.class)
@ContextConfiguration(classes = {TelemetryController.class, GlobalExceptionHandler.class})
class TelemetryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TelemetryQueryService telemetryQueryService;

    @Test
    @SneakyThrows
    void getAllPackets_returns200(){
        Page<TelemetryPacketResponse> page = new PageImpl<>(List.of());

        when(telemetryQueryService.getAllPackets(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/telemetry"))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getPacketByDevice_returns200(){
        Page<TelemetryPacketResponse> page = new PageImpl<>(List.of());

        when(telemetryQueryService.getPacketByDevice(eq("SAT-001"), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/telemetry/SAT-001"))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getPacketByDevice_returns404(){
        when(telemetryQueryService.getPacketByDevice(eq("SAT-001"), any()))
                .thenThrow(new ResourceNotFoundException("Device Not Found"));

        mockMvc.perform(get("/api/v1/telemetry/SAT-001"))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getPacketBetweenRange_returns200(){
        Page<TelemetryPacketResponse> page = new PageImpl<>(List.of());
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        when(telemetryQueryService.getPacketByDeviceAndTimeRange(any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/telemetry/SAT-001/range")
                .param("start", start.toString())
                .param("end", end.toString()))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getPacketBetweenRange_returns404(){
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        when(telemetryQueryService.getPacketByDeviceAndTimeRange(any(), any(), any(), any()))
                .thenThrow(new ResourceNotFoundException("Device Not Found"));

        mockMvc.perform(get("/api/v1/telemetry/SAT-001/range")
                .param("start", start.toString())
                .param("end", end.toString()))
                .andExpect(status().isNotFound());
    }

}
