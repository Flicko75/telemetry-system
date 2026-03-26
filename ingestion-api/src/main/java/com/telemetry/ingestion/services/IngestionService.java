package com.telemetry.ingestion.services;

import com.telemetry.common.models.TelemetryPacket;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class IngestionService {

    private final KafkaTemplate<String, TelemetryPacket> kafkaTemplate;

    public ResponseEntity<String> receivePacket(TelemetryPacket packet) {
        packet.setReceivingTime(LocalDateTime.now());

        kafkaTemplate.send("telemetry.raw", packet.getDeviceId(), packet);

        return ResponseEntity.ok("Packet received successfully");
    }

}
