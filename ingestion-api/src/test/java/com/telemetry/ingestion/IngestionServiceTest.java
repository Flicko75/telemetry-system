package com.telemetry.ingestion;


import com.telemetry.common.enums.DeviceHealth;
import com.telemetry.common.exceptions.RateLimitExceededException;
import com.telemetry.common.models.Coordinates;
import com.telemetry.common.models.TelemetryPacket;
import com.telemetry.ingestion.services.IngestionService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngestionServiceTest {

    @Mock
    private KafkaTemplate<String, TelemetryPacket> kafkaTemplate;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private MeterRegistry meterRegistry = new SimpleMeterRegistry();

    private IngestionService ingestionService;

    @BeforeEach
    void setUp() {
        ingestionService = new IngestionService(kafkaTemplate, redisTemplate, meterRegistry);
    }

    @Test
    void receivePacket_rateLimitExceeded(){
        TelemetryPacket packet = buildPacket(
                "SAT-001",
                Map.of("battery", 50),
                DeviceHealth.HEALTHY,
                LocalDateTime.now()
        );

        String redisKey = "rate:" + packet.getDeviceId();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(redisKey)).thenReturn(26L);

        assertThrows(RateLimitExceededException.class,
                () -> ingestionService.receivePacket(packet));

        verify(kafkaTemplate, never()).send(any(), any(), any());
    }

    @Test
    void receivePacket_rateLimitNotExceeded(){
        TelemetryPacket packet = buildPacket(
                "SAT-001",
                Map.of("battery", 50),
                DeviceHealth.HEALTHY,
                LocalDateTime.now()
        );

        String redisKey = "rate:" + packet.getDeviceId();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(redisKey)).thenReturn(25L);
        when(kafkaTemplate.send(anyString(), anyString(), any(TelemetryPacket.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        ingestionService.receivePacket(packet);

        assertNotNull(packet.getReceivingTime());

        verify(kafkaTemplate).send("telemetry.raw", packet.getDeviceId(), packet);
    }

    @Test
    void receivePacket_firstPacket(){
        TelemetryPacket packet = buildPacket(
                "SAT-001",
                Map.of("battery", 50),
                DeviceHealth.HEALTHY,
                LocalDateTime.now()
        );

        String redisKey = "rate:" + packet.getDeviceId();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(redisKey)).thenReturn(1L);
        when(kafkaTemplate.send(anyString(), anyString(), any(TelemetryPacket.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        ingestionService.receivePacket(packet);

        assertNotNull(packet.getReceivingTime());

        verify(redisTemplate).expire(redisKey, 60, TimeUnit.SECONDS);
        verify(kafkaTemplate).send("telemetry.raw", packet.getDeviceId(), packet);
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
