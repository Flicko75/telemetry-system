package com.telemetry.monitoring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telemetry.common.DTOs.DeviceResponse;
import com.telemetry.common.DTOs.DeviceUpdateDTO;
import com.telemetry.common.enums.DeviceHealth;
import com.telemetry.common.exceptions.ResourceNotFoundException;
import com.telemetry.monitoring.controllers.DeviceController;
import com.telemetry.monitoring.exceptions.GlobalExceptionHandler;
import com.telemetry.monitoring.services.DeviceLiveService;
import com.telemetry.monitoring.services.DeviceService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeviceController.class)
@ContextConfiguration(classes = {DeviceController.class, GlobalExceptionHandler.class})
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeviceService deviceService;

    @MockitoBean
    private DeviceLiveService deviceLiveService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void subscribe_returns200(){
        when(deviceLiveService.subscribe("SAT-001")).thenReturn(new SseEmitter());

        mockMvc.perform(get("/api/v1/devices/SAT-001/live"))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void subscribe_returns404(){
        when(deviceLiveService.subscribe("SAT-001")).thenThrow(new ResourceNotFoundException("Device Not Found"));

        mockMvc.perform(get("/api/v1/devices/SAT-001/live"))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void registerDevice_returns200(){
        DeviceUpdateDTO dto = new DeviceUpdateDTO(
                "Device 001",
                LocalDateTime.now()
        );
        DeviceResponse response = new DeviceResponse(
                "SAT-001",
                "Device",
                DeviceHealth.HEALTHY,
                LocalDateTime.now(),
                LocalDateTime.now().minusDays(1),
                null
        );

        when(deviceService.officiallyRegisterDevice("SAT-001", dto)).thenReturn(response);

        mockMvc.perform(patch("/api/v1/devices/SAT-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void registerDevice_returns404(){
        DeviceUpdateDTO dto = new DeviceUpdateDTO(
                "Device 001",
                LocalDateTime.now()
        );

        when(deviceService.officiallyRegisterDevice("SAT-001", dto))
                .thenThrow(new ResourceNotFoundException("Device Not Found"));

        mockMvc.perform(patch("/api/v1/devices/SAT-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

}
