package com.telemetry.processing;

import com.telemetry.common.enums.DeviceHealth;
import com.telemetry.common.models.Coordinates;
import com.telemetry.common.models.TelemetryPacket;
import com.telemetry.processing.services.DeviceStateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DeviceStateServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private HashOperations<String, String, String> hashOperations;

    @InjectMocks
    private DeviceStateService deviceStateService;

    @BeforeEach
    void hashOps(){
        doReturn(hashOperations).when(redisTemplate).opsForHash();
    }

    @Test
    void updateDeviceState_check(){
        TelemetryPacket packet = buildPacket(
                "SAT-001",
                Map.of("battery", 60),
                DeviceHealth.HEALTHY,
                LocalDateTime.now()
        );
        packet.setReceivingTime(LocalDateTime.now());

        deviceStateService.updateDeviceState(packet);

        verify(hashOperations).put("device:SAT-001", "battery", String.valueOf(60));
        verify(hashOperations).put("device:SAT-001", "health", String.valueOf(DeviceHealth.HEALTHY));
        verify(hashOperations).put("device:SAT-001", "last_seen", String.valueOf(packet.getReceivingTime()));
        verify(hashOperations).put("device:SAT-001", "latitude", String.valueOf(packet.getCoordinates().getLatitude()));
        verify(hashOperations).put("device:SAT-001", "longitude", String.valueOf(packet.getCoordinates().getLongitude()));
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
