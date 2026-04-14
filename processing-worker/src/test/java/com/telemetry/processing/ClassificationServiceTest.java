package com.telemetry.processing;

import com.telemetry.common.enums.DeviceHealth;
import com.telemetry.common.enums.SeverityLevel;
import com.telemetry.common.models.Coordinates;
import com.telemetry.common.models.TelemetryPacket;
import com.telemetry.processing.services.ClassificationService;
import com.telemetry.processing.utils.ClassifyHelper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class ClassificationServiceTest {

    @Mock
    private ClassifyHelper classifyHelper;

    private MeterRegistry meterRegistry = new SimpleMeterRegistry();

    private ClassificationService classificationService;

    @BeforeEach
    void setup(){
        classificationService = new ClassificationService(classifyHelper, meterRegistry);
    }

    @Test
    void classify_oneNormalOneCritical_returnsCritical(){
        TelemetryPacket packet = buildPacket(
                "SAT-001",
                Map.of("battery", 60, "temperature", -10),
                DeviceHealth.HEALTHY,
                LocalDateTime.now()
        );

        when(classifyHelper.classifyMeasurement("battery", 60)).thenReturn(SeverityLevel.NORMAL);
        when(classifyHelper.classifyMeasurement("temperature", -10)).thenReturn(SeverityLevel.CRITICAL);

        assertEquals(SeverityLevel.CRITICAL, classificationService.classify(packet));
    }

    @Test
    void classify_allNormal_returnsNormal(){
        TelemetryPacket packet = buildPacket(
                "SAT-001",
                Map.of("battery", 60, "temperature", 20),
                DeviceHealth.HEALTHY,
                LocalDateTime.now()
        );

        when(classifyHelper.classifyMeasurement("battery", 60)).thenReturn(SeverityLevel.NORMAL);
        when(classifyHelper.classifyMeasurement("temperature", 20)).thenReturn(SeverityLevel.NORMAL);

        assertEquals(SeverityLevel.NORMAL, classificationService.classify(packet));
    }

    @Test
    void classify_oneNearCriticalOneCritical_returnsCritical(){
        TelemetryPacket packet = buildPacket(
                "SAT-001",
                Map.of("battery", 50, "temperature", -10),
                DeviceHealth.HEALTHY,
                LocalDateTime.now()
        );

        when(classifyHelper.classifyMeasurement("battery", 50)).thenReturn(SeverityLevel.NEAR_CRITICAL);
        when(classifyHelper.classifyMeasurement("temperature", -10)).thenReturn(SeverityLevel.CRITICAL);

        assertEquals(SeverityLevel.CRITICAL, classificationService.classify(packet));
    }

    @Test
    void classify_oneMeasurement_returnsSeverity(){
        TelemetryPacket packet = buildPacket(
                "SAT-001",
                Map.of("battery", 60),
                DeviceHealth.HEALTHY,
                LocalDateTime.now()
        );

        when(classifyHelper.classifyMeasurement("battery", 60)).thenReturn(SeverityLevel.NORMAL);

        assertEquals(SeverityLevel.NORMAL, classificationService.classify(packet));
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
