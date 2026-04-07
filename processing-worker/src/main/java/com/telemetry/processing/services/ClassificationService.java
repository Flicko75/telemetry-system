package com.telemetry.processing.services;

import com.telemetry.common.enums.SeverityLevel;
import com.telemetry.common.models.TelemetryPacket;
import com.telemetry.processing.config.ThresholdConfig;
import com.telemetry.processing.utils.ClassifyHelper;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClassificationService {

    private final ClassifyHelper classifyHelper;

    private final MeterRegistry meterRegistry;

    public SeverityLevel classify(TelemetryPacket packet){
        SeverityLevel worstSeverity = SeverityLevel.NORMAL;

        for (Map.Entry<String, Object> entry : packet.getMeasurements().entrySet()){
            SeverityLevel current = classifyHelper.classifyMeasurement(entry.getKey(), entry.getValue());

            if (current.ordinal() > worstSeverity.ordinal()){
                worstSeverity = current;
            }
        }
        meterRegistry.counter("telemetry.packets.classified", "severity", worstSeverity.name()).increment();
        log.info("Device {} classified as {}", packet.getDeviceId(), worstSeverity);

        return worstSeverity;
    }

}