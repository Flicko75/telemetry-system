package com.telemetry.alert.consumers;

import com.telemetry.alert.services.AlertService;
import com.telemetry.common.enums.SeverityLevel;
import com.telemetry.common.models.TelemetryPacket;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlertConsumer {

    private final AlertService alertService;

    @KafkaListener(topics = {"telemetry.near-critical", "telemetry.critical"}, groupId = "alert-worker")
    public void consume(TelemetryPacket packet, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic){
        SeverityLevel severityLevel = topic.equals("telemetry.critical") ? SeverityLevel.CRITICAL : SeverityLevel.NEAR_CRITICAL;
        alertService.createAlertIfNeeded(packet.getDeviceId(), severityLevel);
    }

}
