package com.telemetry.processing;

import com.telemetry.common.enums.DeviceHealth;
import com.telemetry.common.enums.SeverityLevel;
import com.telemetry.common.models.Coordinates;
import com.telemetry.common.models.TelemetryPacket;
import com.telemetry.processing.services.RoutingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoutingServiceTest {

    @Mock
    private KafkaTemplate<String, TelemetryPacket> kafkaTemplate;

    @InjectMocks
    private RoutingService routingService;

    @Test
    void routing_normal(){
        SeverityLevel severityLevel = SeverityLevel.NORMAL;

        TelemetryPacket packet = buildPacket(
                "SAT-001",
                Map.of(),
                DeviceHealth.HEALTHY,
                LocalDateTime.now()
        );

        routingService.route(packet, severityLevel);

        verify(kafkaTemplate).send("telemetry.normal", packet.getDeviceId(), packet);
    }

    @Test
    void routing_nearCritical(){
        SeverityLevel severityLevel = SeverityLevel.NEAR_CRITICAL;

        TelemetryPacket packet = buildPacket(
                "SAT-001",
                Map.of(),
                DeviceHealth.HEALTHY,
                LocalDateTime.now()
        );

        routingService.route(packet, severityLevel);

        verify(kafkaTemplate).send("telemetry.near-critical", packet.getDeviceId(), packet);
    }

    @Test
    void routing_critical(){
        SeverityLevel severityLevel = SeverityLevel.CRITICAL;

        TelemetryPacket packet = buildPacket(
                "SAT-001",
                Map.of(),
                DeviceHealth.HEALTHY,
                LocalDateTime.now()
        );

        routingService.route(packet, severityLevel);

        verify(kafkaTemplate).send("telemetry.critical", packet.getDeviceId(), packet);
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
