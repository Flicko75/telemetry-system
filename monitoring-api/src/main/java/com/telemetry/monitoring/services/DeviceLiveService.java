package com.telemetry.monitoring.services;

import com.telemetry.monitoring.entity.DeviceEntity;
import com.telemetry.monitoring.entity.TelemetryPacketEntity;
import com.telemetry.monitoring.repos.DeviceRepository;
import com.telemetry.monitoring.repos.TelemetryPacketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class DeviceLiveService {

    private final RedisTemplate<String, String> redisTemplate;

    private Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    private final DeviceRepository deviceRepository;

    private final TelemetryPacketRepository packetRepository;

    public SseEmitter subscribe(String deviceId){
        Map<Object, Object> state = redisTemplate.opsForHash().entries("device:" + deviceId);

        if (state.isEmpty()){
            DeviceEntity device = deviceRepository.findByDeviceId(deviceId)
                    .orElseThrow(() -> new RuntimeException("Device not found: " + deviceId));

            TelemetryPacketEntity packet = packetRepository.findTopByDeviceOrderByReceivingTimeDesc(device)
                    .orElseThrow(() -> new RuntimeException("Packet not found"));

            Map<Object, Object> stateFromDb = new HashMap<>();
            stateFromDb.put("health", String.valueOf(packet.getDevice().getDeviceHealth()));
            stateFromDb.put("last_seen", String.valueOf(packet.getReceivingTime()));
            stateFromDb.put("latitude", String.valueOf(packet.getLatitude()));
            stateFromDb.put("longitude", String.valueOf(packet.getLongitude()));
            packet.getMeasurements().forEach((k, v) -> stateFromDb.put(k, String.valueOf(v)));

            state = stateFromDb;
        }

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(deviceId, emitter);

        emitter.onCompletion(() -> emitters.remove(deviceId));
        emitter.onTimeout(() -> emitters.remove(deviceId));

        sendToEmitter(emitter, state);

        return emitter;
    }

    @Scheduled(fixedRate = 5000)
    public void pushUpdates(){
        emitters.forEach((deviceId, emitter) -> {
            Map<Object, Object> state = redisTemplate.opsForHash().entries("device:" + deviceId);
            if (!state.isEmpty()){
                sendToEmitter(emitter, state);
            }
        });
    }

    private void sendToEmitter(SseEmitter emitter, Map<Object, Object> state){
        try {
            emitter.send(SseEmitter.event().data(state));
        } catch (IOException e){
            emitter.complete();
        }
    }

}
