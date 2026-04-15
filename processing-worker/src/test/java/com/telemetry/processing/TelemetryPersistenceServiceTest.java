package com.telemetry.processing;

import com.telemetry.common.enums.DeviceHealth;
import com.telemetry.common.enums.SeverityLevel;
import com.telemetry.common.models.Coordinates;
import com.telemetry.common.models.TelemetryPacket;
import com.telemetry.processing.entity.DeviceEntity;
import com.telemetry.processing.entity.TelemetryPacketEntity;
import com.telemetry.processing.repos.DeviceRepository;
import com.telemetry.processing.repos.TelemetryPacketRepository;
import com.telemetry.processing.services.TelemetryPersistenceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelemetryPersistenceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private TelemetryPacketRepository packetRepository;

    @InjectMocks
    private TelemetryPersistenceService persistenceService;

    @Test
    void persistPacket_deviceAlreadyExists(){
        String deviceId = "SAT-001";
        SeverityLevel severityLevel = SeverityLevel.NORMAL;

        DeviceEntity device = buildDevice(
                deviceId,
                "Device 001",
                DeviceHealth.HEALTHY
        );

        TelemetryPacket packet = buildPacket(
                deviceId,
                Map.of(),
                DeviceHealth.HEALTHY,
                LocalDateTime.now()
        );
        packet.setReceivingTime(LocalDateTime.now());

        when(deviceRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(device));

        persistenceService.persist(packet, severityLevel);

        ArgumentCaptor<TelemetryPacketEntity> packetCaptor = ArgumentCaptor.forClass(TelemetryPacketEntity.class);
        ArgumentCaptor<DeviceEntity> deviceCaptor = ArgumentCaptor.forClass(DeviceEntity.class);

        verify(deviceRepository).save(deviceCaptor.capture());
        verify(packetRepository).save(packetCaptor.capture());

        DeviceEntity savedDevice = deviceCaptor.getValue();
        assertEquals(packet.getDeviceHealth(), savedDevice.getDeviceHealth());
        assertEquals(packet.getReceivingTime(), savedDevice.getLastSeen());

        TelemetryPacketEntity savedPacket = packetCaptor.getValue();
        assertEquals(severityLevel, savedPacket.getSeverityLevel());
    }

    @Test
    void persistPacket_deviceDoesntExist(){
        String deviceId = "SAT-001";
        SeverityLevel severityLevel = SeverityLevel.NORMAL;

        TelemetryPacket packet = buildPacket(
                deviceId,
                Map.of(),
                DeviceHealth.HEALTHY,
                LocalDateTime.now()
        );

        when(deviceRepository.findByDeviceId(deviceId)).thenReturn(Optional.empty());

        persistenceService.persist(packet, severityLevel);

        ArgumentCaptor<TelemetryPacketEntity> packetCaptor = ArgumentCaptor.forClass(TelemetryPacketEntity.class);
        ArgumentCaptor<DeviceEntity> deviceCaptor = ArgumentCaptor.forClass(DeviceEntity.class);

        verify(deviceRepository).save(deviceCaptor.capture());
        verify(packetRepository).save(packetCaptor.capture());

        DeviceEntity savedDevice = deviceCaptor.getValue();
        assertEquals("Auto registered device", savedDevice.getDeviceDesc());
        assertNotNull(savedDevice.getRegisteredAt());
        assertEquals(deviceId, savedDevice.getDeviceId());
        assertEquals(packet.getDeviceHealth(), savedDevice.getDeviceHealth());
        assertEquals(packet.getReceivingTime(), savedDevice.getLastSeen());

        TelemetryPacketEntity savedPacket = packetCaptor.getValue();
        assertEquals(severityLevel, savedPacket.getSeverityLevel());
    }

    private DeviceEntity buildDevice(String deviceId, String deviceDesc, DeviceHealth health){
        DeviceEntity device = new DeviceEntity();
        device.setDeviceId(deviceId);
        device.setDeviceDesc(deviceDesc);
        device.setDeviceHealth(health);
        device.setRegisteredAt(LocalDateTime.now());
        return device;
    }

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

}
