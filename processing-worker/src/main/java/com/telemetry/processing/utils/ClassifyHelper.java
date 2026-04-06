package com.telemetry.processing.utils;

import com.telemetry.common.enums.SeverityLevel;
import com.telemetry.processing.config.ThresholdConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClassifyHelper {

    private final ThresholdConfig thresholdConfig;

    public SeverityLevel classifyMeasurement(String key, Object value){
        SeverityLevel currentSeverity = SeverityLevel.NORMAL;

        if (value instanceof Number){
            double numericValue = ((Number) value).doubleValue();

            if (key.equals("battery")){
                double batteryCritical = thresholdConfig.getBattery().getCritical();
                double batteryNearCritical = thresholdConfig.getBattery().getNearCritical();

                if (numericValue <= batteryNearCritical &&
                    numericValue > batteryCritical){
                    currentSeverity = SeverityLevel.NEAR_CRITICAL;
                }
                else if (numericValue <= batteryCritical){
                    currentSeverity = SeverityLevel.CRITICAL;
                }
            }
            else if (key.equals("temperature")) {
                double temperatureCriticalHigh = thresholdConfig.getTemperature().getCriticalHigh();
                double temperatureNearCriticalHigh = thresholdConfig.getTemperature().getNearCriticalHigh();
                double temperatureCriticalLow = thresholdConfig.getTemperature().getCriticalLow();
                double temperatureNearCriticalLow = thresholdConfig.getTemperature().getNearCriticalLow();

                if (numericValue >= temperatureCriticalHigh ||
                        numericValue <= temperatureCriticalLow){
                    currentSeverity = SeverityLevel.CRITICAL;
                }
                else if ((numericValue >= temperatureNearCriticalHigh && numericValue < temperatureCriticalHigh) ||
                        (numericValue <= temperatureNearCriticalLow && numericValue > temperatureCriticalLow)) {
                    currentSeverity = SeverityLevel.NEAR_CRITICAL;
                }
            }
            else if (key.equals("pressure")) {
                double pressureCriticalHigh = thresholdConfig.getPressure().getCriticalHigh();
                double pressureNearCriticalHigh = thresholdConfig.getPressure().getNearCriticalHigh();
                double pressureCriticalLow = thresholdConfig.getPressure().getCriticalLow();
                double pressureNearCriticalLow = thresholdConfig.getPressure().getNearCriticalLow();

                if (numericValue >= pressureCriticalHigh ||
                        numericValue <= pressureCriticalLow){
                    currentSeverity = SeverityLevel.CRITICAL;
                }
                else if ((numericValue >= pressureNearCriticalHigh && numericValue < pressureCriticalHigh) ||
                        (numericValue <= pressureNearCriticalLow && numericValue > pressureCriticalLow)) {
                    currentSeverity = SeverityLevel.NEAR_CRITICAL;
                }
            }
            else {
                log.warn("Unknown measurement key {} defaulting to NEAR_CRITICAL", key);
                currentSeverity = SeverityLevel.NEAR_CRITICAL;
            }
        }
        else {
            log.warn("Non-numeric value of key {} defaulting to NEAR_CRITICAL", key);
            currentSeverity = SeverityLevel.NEAR_CRITICAL;
        }

        return currentSeverity;
    }

}
