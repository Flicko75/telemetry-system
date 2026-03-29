package com.telemetry.processing.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "telemetry.thresholds")
@Getter
@Setter
public class ThresholdConfig {

    private BatteryThreshold battery;
    private TemperatureThreshold temperature;
    private PressureThreshold pressure;

    @Getter
    @Setter
    public static class BatteryThreshold {
        private double critical;
        private double nearCritical;
    }

    @Getter
    @Setter
    public static class TemperatureThreshold {
        private double criticalHigh;
        private double nearCriticalHigh;
        private double criticalLow;
        private double nearCriticalLow;
    }

    @Getter
    @Setter
    public static class PressureThreshold {
        private double criticalHigh;
        private double nearCriticalHigh;
        private double criticalLow;
        private double nearCriticalLow;
    }
}
