package com.telemetry.processing.services;

import com.telemetry.common.models.TelemetryPacket;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class DeviceStateService {

    private final RedisTemplate<String, String> redisTemplate;

    public void updateDeviceState(TelemetryPacket packet){
        String redisKey = "device:" + packet.getDeviceId();
        packet.getMeasurements().forEach((key, value) -> {
            redisTemplate.opsForHash().put(redisKey, key, String.valueOf(value));
        });
        redisTemplate.opsForHash().put(redisKey, "health", String.valueOf(packet.getDeviceHealth()));
        redisTemplate.opsForHash().put(redisKey, "last_seen", String.valueOf(packet.getReceivingTime()));
        redisTemplate.opsForHash().put(redisKey, "latitude", String.valueOf(packet.getCoordinates().getLatitude()));
        redisTemplate.opsForHash().put(redisKey, "longitude", String.valueOf(packet.getCoordinates().getLongitude()));
    }

}
