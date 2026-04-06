package com.telemetry.ingestion.services;

import com.telemetry.common.models.TelemetryPacket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class IngestionService {

    private final KafkaTemplate<String, TelemetryPacket> kafkaTemplate;

    public ResponseEntity<String> receivePacket(TelemetryPacket packet) {
        packet.setReceivingTime(LocalDateTime.now());

        kafkaTemplate.send("telemetry.raw", packet.getDeviceId(), packet)
                .whenComplete((result, ex) -> {
                    if (ex != null)
                        log.error("Failed to send packet for device {}", packet.getDeviceId(), ex);
                    else
                        log.info("Packet received for device {}", packet.getDeviceId());
                });

        return ResponseEntity.ok("Packet received successfully");
    }

}
