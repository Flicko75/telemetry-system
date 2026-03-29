package com.telemetry.processing.services;

import com.telemetry.common.enums.SeverityLevel;
import com.telemetry.common.models.TelemetryPacket;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoutingService {

    private final KafkaTemplate<String, TelemetryPacket> kafkaTemplate;

    public void route(TelemetryPacket packet, SeverityLevel severityLevel) {
        switch (severityLevel) {
            case NORMAL -> kafkaTemplate.send("telemetry.normal", packet.getDeviceId(), packet);
            case NEAR_CRITICAL -> kafkaTemplate.send("telemetry.near-critical", packet.getDeviceId(), packet);
            case CRITICAL -> kafkaTemplate.send("telemetry.critical", packet.getDeviceId(), packet);
        }
    }

}
