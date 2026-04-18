package com.telemetry.monitoring;

import com.telemetry.common.DTOs.AlertEntityDTO;
import com.telemetry.common.DTOs.AlertResponse;
import com.telemetry.common.enums.AlertStatus;
import com.telemetry.common.enums.DeviceHealth;
import com.telemetry.common.enums.SeverityLevel;
import com.telemetry.common.exceptions.ResourceNotFoundException;
import com.telemetry.monitoring.entity.AlertEntity;
import com.telemetry.monitoring.entity.DeviceEntity;
import com.telemetry.monitoring.repos.AlertRepository;
import com.telemetry.monitoring.repos.DeviceRepository;
import com.telemetry.monitoring.services.AlertService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private AlertService alertService;

    @Test
    void getAlertsByDevice_success(){
        String deviceId = "SAT-001";

        DeviceEntity device = buildDevice(deviceId, "Device 001", DeviceHealth.HEALTHY);
        AlertEntity alert = buildAlert(device, SeverityLevel.CRITICAL, AlertStatus.ACTIVE);

        when(deviceRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(device));
        when(alertRepository.findByDevice(device)).thenReturn(List.of(alert));

        List<AlertResponse> result = alertService.getAlertsByDevice(deviceId);

        assertEquals(1, result.size());
        assertEquals(deviceId, result.getFirst().deviceId());
        assertEquals(SeverityLevel.CRITICAL, result.getFirst().severityLevel());
        assertEquals(AlertStatus.ACTIVE, result.getFirst().alertStatus());

        verify(deviceRepository).findByDeviceId(deviceId);
        verify(alertRepository).findByDevice(device);
    }

    @Test
    void getAlertsByDevice_deviceNotFound(){
        String deviceId = "SAT-001";

        when(deviceRepository.findByDeviceId(deviceId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> alertService.getAlertsByDevice(deviceId));

        verify(deviceRepository).findByDeviceId(deviceId);
        verify(alertRepository, never()).findByDevice(any());
    }

    @Test
    void resolveAlert_success(){
        Long alertId = 1L;
        AlertEntityDTO dto = new AlertEntityDTO(AlertStatus.RESOLVED);

        DeviceEntity device = buildDevice("SAT-001", "Device 001", DeviceHealth.HEALTHY);
        AlertEntity alert = buildAlert(device, SeverityLevel.CRITICAL, AlertStatus.ACTIVE);

        when(alertRepository.findById(alertId)).thenReturn(Optional.of(alert));
        when(alertRepository.save(alert)).thenReturn(alert);

        alertService.resolveAlert(alertId, dto);

        ArgumentCaptor<AlertEntity> captor = ArgumentCaptor.forClass(AlertEntity.class);

        verify(alertRepository).save(captor.capture());

        AlertEntity saved = captor.getValue();
        assertEquals(dto.status(), saved.getAlertStatus());
        assertNotNull(saved.getResolvedAt());
    }

    @Test
    void resolveAlert_alertNotFound(){
        Long alertId = 1L;
        AlertEntityDTO dto = new AlertEntityDTO(AlertStatus.RESOLVED);

        when(alertRepository.findById(alertId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> alertService.resolveAlert(alertId, dto));

        verify(alertRepository).findById(alertId);
        verify(alertRepository, never()).save(any());
    }

    private DeviceEntity buildDevice(String deviceId, String deviceDesc, DeviceHealth health){
        DeviceEntity device = new DeviceEntity();
        device.setDeviceId(deviceId);
        device.setDeviceDesc(deviceDesc);
        device.setDeviceHealth(health);
        device.setRegisteredAt(LocalDateTime.now());
        return device;
    }

    private AlertEntity buildAlert(DeviceEntity device, SeverityLevel severityLevel, AlertStatus alertStatus){
        AlertEntity alert = new AlertEntity();
        alert.setDevice(device);
        alert.setAlertStatus(alertStatus);
        alert.setSeverityLevel(severityLevel);
        alert.setCreatedAt(LocalDateTime.now());

        return alert;
    }

}
