package com.telemetry.ingestion.services;

import com.telemetry.common.exceptions.RateLimitExceededException;
import com.telemetry.common.models.TelemetryPacket;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class IngestionService {

    private final KafkaTemplate<String, TelemetryPacket> kafkaTemplate;

    private final RedisTemplate<String, String> redisTemplate;

    private final MeterRegistry meterRegistry;

    public ResponseEntity<String> receivePacket(TelemetryPacket packet) {
        checkRateLimit(packet.getDeviceId());

        packet.setReceivingTime(LocalDateTime.now());

        kafkaTemplate.send("telemetry.raw", packet.getDeviceId(), packet)
                .whenComplete((result, ex) -> {
                    if (ex != null)
                        log.error("Failed to send packet for device {}", packet.getDeviceId(), ex);
                    else {
                        log.info("Packet received for device {}", packet.getDeviceId());
                        meterRegistry.counter("telemetry.packet.received", "deviceId", packet.getDeviceId()).increment();
                    }
                });

        return ResponseEntity.ok("Packet received successfully");
    }

    private void checkRateLimit(String deviceId){
        String redisKey = "rate:" + deviceId;

        Long count = redisTemplate.opsForValue().increment(redisKey);

        if (count != null && count == 1){
            redisTemplate.expire(redisKey, 60, TimeUnit.SECONDS);
        }

        if (count ==  null || count > 25){
            meterRegistry.counter("telemetry.rate.limit.exceeded", "deviceId", deviceId).increment();
            log.warn("Rate limit exceeded for device {}", deviceId);
            throw new RateLimitExceededException("Rate limit exceeded for device:" + deviceId);
        }
    }

}
