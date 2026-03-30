package com.telemetry.processing.consumer;

import com.telemetry.common.enums.SeverityLevel;
import com.telemetry.common.models.TelemetryPacket;
import com.telemetry.processing.services.ClassificationService;
import com.telemetry.processing.services.RoutingService;
import com.telemetry.processing.services.TelemetryPersistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelemetryConsumer {

    private final ClassificationService classificationService;

    private final RoutingService routingService;

    private final TelemetryPersistenceService persistenceService;

    @KafkaListener(topics = "telemetry.raw", groupId = "processing-group")
    public void consume(TelemetryPacket packet){

        SeverityLevel severityLevel = classificationService.classify(packet);
        persistenceService.persist(packet, severityLevel);
        routingService.route(packet, severityLevel);
    }

}
