package com.telemetry.processing.services;

import com.telemetry.common.enums.SeverityLevel;
import com.telemetry.common.models.TelemetryPacket;
import com.telemetry.processing.config.ThresholdConfig;
import com.telemetry.processing.utils.ClassifyHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ClassificationService {

    private final ClassifyHelper classifyHelper;

    public SeverityLevel classify(TelemetryPacket packet){
        SeverityLevel worstSeverity = SeverityLevel.NORMAL;

        for (Map.Entry<String, Object> entry : packet.getMeasurements().entrySet()){
            SeverityLevel current = classifyHelper.classifyMeasurement(entry.getKey(), entry.getValue());

            if (current.ordinal() > worstSeverity.ordinal()){
                worstSeverity = current;
            }
        }

        return worstSeverity;
    }

}