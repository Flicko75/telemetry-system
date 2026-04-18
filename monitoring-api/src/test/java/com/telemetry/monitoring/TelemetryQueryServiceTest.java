package com.telemetry.monitoring;

import com.telemetry.common.DTOs.TelemetryPacketResponse;
import com.telemetry.common.enums.DeviceHealth;
import com.telemetry.common.exceptions.ResourceNotFoundException;
import com.telemetry.monitoring.entity.DeviceEntity;
import com.telemetry.monitoring.entity.TelemetryPacketEntity;
import com.telemetry.monitoring.repos.DeviceRepository;
import com.telemetry.monitoring.repos.TelemetryPacketRepository;
import com.telemetry.monitoring.services.TelemetryQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelemetryQueryServiceTest {

    @Mock
    private TelemetryPacketRepository packetRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private TelemetryQueryService queryService;

    @Test
    void getAllPackets_success(){
        Pageable pageable = PageRequest.of(0, 5);

        DeviceEntity device = buildDevice(
                "SAT-001",
                "device 001",
                DeviceHealth.DEGRADED
        );

        TelemetryPacketEntity packet = buildPacket(
                device,
                Map.of()
        );

        Page<TelemetryPacketEntity> page = new PageImpl<>(List.of(packet));

        when(packetRepository.findAll(pageable)).thenReturn(page);

        Page<TelemetryPacketResponse> result = queryService.getAllPackets(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("SAT-001", result.getContent().getFirst().deviceId());

        verify(packetRepository).findAll(pageable);
    }

    @Test
    void getPacketByDevice_success(){
        String deviceId = "SAT-001";
        Pageable pageable = PageRequest.of(0, 5);

        DeviceEntity device = buildDevice(
                "SAT-001",
                "device 001",
                DeviceHealth.DEGRADED
        );

        TelemetryPacketEntity packet = buildPacket(
                device,
                Map.of()
        );

        Page<TelemetryPacketEntity> page = new PageImpl<>(List.of(packet));

        when(deviceRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(device));
        when(packetRepository.findByDevice(device, pageable)).thenReturn(page);

        Page<TelemetryPacketResponse> result = queryService.getPacketByDevice(deviceId, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("SAT-001", result.getContent().getFirst().deviceId());

        verify(deviceRepository).findByDeviceId(deviceId);
        verify(packetRepository).findByDevice(device, pageable);
    }

    @Test
    void getPacketByDevice_deviceNotFound(){
        String deviceId = "SAT-001";
        Pageable pageable = PageRequest.of(0, 5);

        when(deviceRepository.findByDeviceId(deviceId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> queryService.getPacketByDevice(deviceId, pageable));

        verify(deviceRepository).findByDeviceId(deviceId);
        verify(packetRepository, never()).findByDevice(any(), any());
    }

    @Test
    void getPacketByDeviceAndTimeRange_success(){
        String deviceId = "SAT-001";
        Pageable pageable = PageRequest.of(0, 5);
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        DeviceEntity device = buildDevice(
                "SAT-001",
                "device 001",
                DeviceHealth.DEGRADED
        );

        TelemetryPacketEntity packet = buildPacket(
                device,
                Map.of()
        );

        Page<TelemetryPacketEntity> page = new PageImpl<>(List.of(packet));

        when(deviceRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(device));
        when(packetRepository.findByDeviceAndReceivingTimeBetween(device, start, end, pageable)).thenReturn(page);

        Page<TelemetryPacketResponse> result = queryService.getPacketByDeviceAndTimeRange(deviceId, start, end, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("SAT-001", result.getContent().getFirst().deviceId());

        verify(deviceRepository).findByDeviceId(deviceId);
        verify(packetRepository).findByDeviceAndReceivingTimeBetween(device, start, end, pageable);
    }

    @Test
    void getPacketByDeviceAndTimeRange_deviceNotFound(){
        String deviceId = "SAT-001";
        Pageable pageable = PageRequest.of(0, 5);
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        when(deviceRepository.findByDeviceId(deviceId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> queryService.getPacketByDeviceAndTimeRange(deviceId, start, end, pageable));

        verify(deviceRepository).findByDeviceId(deviceId);
        verify(packetRepository, never()).findByDeviceAndReceivingTimeBetween(any(), any(), any(), any());
    }

    private DeviceEntity buildDevice(String deviceId, String deviceDesc, DeviceHealth health){
        DeviceEntity device = new DeviceEntity();
        device.setDeviceId(deviceId);
        device.setDeviceDesc(deviceDesc);
        device.setDeviceHealth(health);
        device.setRegisteredAt(LocalDateTime.now());
        return device;
    }

    private TelemetryPacketEntity buildPacket(DeviceEntity device, Map<String, Object> measurements){
        TelemetryPacketEntity packet = new TelemetryPacketEntity();
        packet.setDevice(device);
        packet.setMeasurements(measurements);
        packet.setLatitude(80d);
        packet.setLongitude(100d);
        packet.setReceivingTime(LocalDateTime.now());
        return packet;
    }

}
