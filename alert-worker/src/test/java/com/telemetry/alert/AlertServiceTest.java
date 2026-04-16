package com.telemetry.alert;

import com.telemetry.alert.entity.AlertEntity;
import com.telemetry.alert.entity.DeviceEntity;
import com.telemetry.alert.repos.AlertRepository;
import com.telemetry.alert.repos.DeviceRepository;
import com.telemetry.alert.services.AlertService;
import com.telemetry.common.enums.AlertStatus;
import com.telemetry.common.enums.DeviceHealth;
import com.telemetry.common.enums.SeverityLevel;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private AlertRepository alertRepository;

    private MeterRegistry meterRegistry = new SimpleMeterRegistry();

    private AlertService alertService;

    @BeforeEach
    void setup(){
        alertService = new AlertService(
                deviceRepository,
                alertRepository,
                meterRegistry
        );
    }

    @Test
    void alertAlreadyExist(){
        String deviceId = "SAT-001";
        SeverityLevel severityLevel = SeverityLevel.CRITICAL;

        DeviceEntity device = buildDevice(
                deviceId,
                "Device 001",
                DeviceHealth.HEALTHY
        );

        AlertEntity alert = buildAlert(
                device,
                SeverityLevel.CRITICAL,
                AlertStatus.ACTIVE
        );

        when(deviceRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(device));
        when(alertRepository.findByDeviceAndAlertStatus(device, AlertStatus.ACTIVE)).thenReturn(Optional.of(alert));

        alertService.createAlertIfNeeded(deviceId, severityLevel);

        verify(alertRepository, never()).save(any());
        verify(deviceRepository).findByDeviceId(deviceId);
        verify(alertRepository).findByDeviceAndAlertStatus(device, AlertStatus.ACTIVE);

        assertEquals(0.0, meterRegistry.counter("telemetry.alert.created", "severity", severityLevel.name()).count());
    }

    @Test
    void alertDoesntExist(){
        String deviceId = "SAT-001";
        SeverityLevel severityLevel = SeverityLevel.CRITICAL;

        DeviceEntity device = buildDevice(
                deviceId,
                "Device 001",
                DeviceHealth.HEALTHY
        );

        when(deviceRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(device));
        when(alertRepository.findByDeviceAndAlertStatus(device, AlertStatus.ACTIVE)).thenReturn(Optional.empty());

        alertService.createAlertIfNeeded(deviceId, severityLevel);

        ArgumentCaptor<AlertEntity> alertCaptor = ArgumentCaptor.forClass(AlertEntity.class);

        verify(alertRepository).save(alertCaptor.capture());

        AlertEntity savedAlert = alertCaptor.getValue();
        assertEquals(device, savedAlert.getDevice());
        assertEquals(AlertStatus.ACTIVE, savedAlert.getAlertStatus());
        assertNotNull(savedAlert.getCreatedAt());
        assertEquals(severityLevel, savedAlert.getSeverityLevel());

        assertEquals(1.0, meterRegistry.counter("telemetry.alert.created", "severity", severityLevel.name()).count());
    }

    private DeviceEntity buildDevice(String deviceId, String deviceDesc, DeviceHealth health){
        DeviceEntity device = new DeviceEntity();
        device.setDeviceId(deviceId);
        device.setDeviceDesc(deviceDesc);
        device.setDeviceHealth(health);
        device.setRegisteredAt(LocalDateTime.now());
        return device;
    }

    private AlertEntity buildAlert(DeviceEntity device, SeverityLevel severityLevel, AlertStatus status){
        AlertEntity alert = new AlertEntity();
        alert.setDevice(device);
        alert.setSeverityLevel(severityLevel);
        alert.setAlertStatus(status);
        alert.setCreatedAt(LocalDateTime.now());

        return alert;
    }

}
