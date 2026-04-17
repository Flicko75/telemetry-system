package com.telemetry.monitoring;

import com.telemetry.common.enums.DeviceHealth;
import com.telemetry.common.exceptions.ResourceNotFoundException;
import com.telemetry.common.models.Coordinates;
import com.telemetry.common.models.TelemetryPacket;
import com.telemetry.monitoring.entity.DeviceEntity;
import com.telemetry.monitoring.entity.TelemetryPacketEntity;
import com.telemetry.monitoring.repos.DeviceRepository;
import com.telemetry.monitoring.repos.TelemetryPacketRepository;
import com.telemetry.monitoring.services.DeviceLiveService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DeviceLiveServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private TelemetryPacketRepository packetRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    private MeterRegistry meterRegistry = new SimpleMeterRegistry();

    private DeviceLiveService deviceLiveService;

    @BeforeEach
    void setup(){
        deviceLiveService = new DeviceLiveService(
                redisTemplate,
                deviceRepository,
                packetRepository,
                meterRegistry
        );

        doReturn(hashOperations).when(redisTemplate).opsForHash();
    }

    @Test
    void redisHasState(){
        String deviceId = "SAT-001";

        Map<Object, Object> state = buildState(
                DeviceHealth.HEALTHY,
                Map.of("battery", 80, "temperature", 40)
        );

        when(hashOperations.entries("device:" + deviceId)).thenReturn(state);

        SseEmitter emitter = deviceLiveService.subscribe(deviceId);

        assertNotNull(emitter);

        verify(hashOperations).entries("device:" + deviceId);
        verify(deviceRepository, never()).findByDeviceId(any());
        verify(packetRepository, never()).findTopByDeviceOrderByReceivingTimeDesc(any());
    }

    @Test
    void redisDoesntHaveState(){
        String deviceId = "SAT-001";

        DeviceEntity device = buildDevice(
                deviceId,
                "device 001",
                DeviceHealth.DEGRADED
        );

        TelemetryPacketEntity packet = buildPacket(
                device,
                Map.of("battery", 60, "temperature", 40)
        );

        when(hashOperations.entries("device:" + deviceId)).thenReturn(Map.of());
        when(deviceRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(device));
        when(packetRepository.findTopByDeviceOrderByReceivingTimeDesc(device)).thenReturn(Optional.of(packet));

        SseEmitter emitter = deviceLiveService.subscribe(deviceId);

        assertNotNull(emitter);

        verify(hashOperations).entries("device:" + deviceId);
        verify(deviceRepository).findByDeviceId(deviceId);
        verify(packetRepository).findTopByDeviceOrderByReceivingTimeDesc(device);
    }

    @Test
    void deviceNotFound(){
        String deviceId = "SAT-001";

        when(hashOperations.entries("device:" + deviceId)).thenReturn(Map.of());
        when(deviceRepository.findByDeviceId(deviceId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> deviceLiveService.subscribe(deviceId));

        verify(hashOperations).entries("device:" + deviceId);
        verify(deviceRepository).findByDeviceId(deviceId);
        verify(packetRepository, never()).findTopByDeviceOrderByReceivingTimeDesc(any());
    }

    @Test
    void packetNotFound(){
        String deviceId = "SAT-001";

        DeviceEntity device = buildDevice(
                deviceId,
                "device 001",
                DeviceHealth.DEGRADED
        );

        when(hashOperations.entries("device:" + deviceId)).thenReturn(Map.of());
        when(deviceRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(device));
        when(packetRepository.findTopByDeviceOrderByReceivingTimeDesc(device)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> deviceLiveService.subscribe(deviceId));

        verify(hashOperations).entries("device:" + deviceId);
        verify(deviceRepository).findByDeviceId(deviceId);
        verify(packetRepository).findTopByDeviceOrderByReceivingTimeDesc(device);
    }

    private Map<Object, Object> buildState(DeviceHealth health, Map<String, Object> measurements){
        Map<Object, Object> state = new HashMap<>();
        state.put("health", String.valueOf(health));
        state.put("last_seen", String.valueOf(LocalDateTime.now()));
        state.put("latitude", String.valueOf(80));
        state.put("longitude", String.valueOf(100));
        measurements.forEach((k, v) -> state.put(k, String.valueOf(v)));

        return state;
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
