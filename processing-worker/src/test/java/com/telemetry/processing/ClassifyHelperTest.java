package com.telemetry.processing;

import com.telemetry.common.enums.SeverityLevel;
import com.telemetry.processing.config.ThresholdConfig;
import com.telemetry.processing.utils.ClassifyHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ClassifyHelperTest {

    @Mock
    private ThresholdConfig thresholdConfig;

    @Mock
    private ThresholdConfig.BatteryThreshold batteryThreshold;

    @Mock
    private ThresholdConfig.TemperatureThreshold temperatureThreshold;

    @Mock
    private ThresholdConfig.PressureThreshold pressureThreshold;

    @InjectMocks
    private ClassifyHelper classifyHelper;

    @BeforeEach
    void setup(){
        when(thresholdConfig.getBattery()).thenReturn(batteryThreshold);
        when(thresholdConfig.getTemperature()).thenReturn(temperatureThreshold);
        when(thresholdConfig.getPressure()).thenReturn(pressureThreshold);

        when(batteryThreshold.getCritical()).thenReturn(20.0);
        when(batteryThreshold.getNearCritical()).thenReturn(50.0);

        when(temperatureThreshold.getCriticalHigh()).thenReturn(80.0);
        when(temperatureThreshold.getNearCriticalHigh()).thenReturn(60.0);
        when(temperatureThreshold.getCriticalLow()).thenReturn(-20.0);
        when(temperatureThreshold.getNearCriticalLow()).thenReturn(0.0);

        when(pressureThreshold.getCriticalHigh()).thenReturn(1100.0);
        when(pressureThreshold.getNearCriticalHigh()).thenReturn(1060.0);
        when(pressureThreshold.getCriticalLow()).thenReturn(900.0);
        when(pressureThreshold.getNearCriticalLow()).thenReturn(950.0);
    }

    @Test
    void battery_aboveNearCritical_returnsNormal(){
        assertEquals(SeverityLevel.NORMAL, classifyHelper.classifyMeasurement("battery", 51.0));
    }

    @Test
    void battery_atNearCritical_returnsNearCritical(){
        assertEquals(SeverityLevel.NEAR_CRITICAL, classifyHelper.classifyMeasurement("battery", 50.0));
    }

    @Test
    void battery_betweenCriticalAndNearCritical_returnsNearCritical(){
        assertEquals(SeverityLevel.NEAR_CRITICAL, classifyHelper.classifyMeasurement("battery", 21.0));
    }

    @Test
    void battery_atCritical_returnsCritical(){
        assertEquals(SeverityLevel.CRITICAL, classifyHelper.classifyMeasurement("battery", 20.0));
    }

    @Test
    void battery_belowCritical_returnsCritical(){
        assertEquals(SeverityLevel.CRITICAL, classifyHelper.classifyMeasurement("battery", 15.0));
    }

    @Test
    void temperature_normal_returnsNormal() {
        assertEquals(SeverityLevel.NORMAL, classifyHelper.classifyMeasurement("temperature", 30.0));
    }

    @Test
    void temperature_belowNearCriticalHigh_returnsNormal(){
        assertEquals(SeverityLevel.NORMAL, classifyHelper.classifyMeasurement("temperature", 59.0));
    }

    @Test
    void temperature_atNearCriticalHigh_returnsNearCritical(){
        assertEquals(SeverityLevel.NEAR_CRITICAL, classifyHelper.classifyMeasurement("temperature", 60.0));
    }

    @Test
    void temperature_belowCriticalHigh_returnsNearCritical() {
        assertEquals(SeverityLevel.NEAR_CRITICAL, classifyHelper.classifyMeasurement("temperature", 70.0));
    }

    @Test
    void temperature_atCriticalHigh_returnsCritical() {
        assertEquals(SeverityLevel.CRITICAL, classifyHelper.classifyMeasurement("temperature", 80.0));
    }

    @Test
    void temperature_aboveNearCriticalLow_returnsNormal(){
        assertEquals(SeverityLevel.NORMAL, classifyHelper.classifyMeasurement("temperature", 1.0));
    }

    @Test
    void temperature_atNearCriticalLow_returnsNearCritical(){
        assertEquals(SeverityLevel.NEAR_CRITICAL, classifyHelper.classifyMeasurement("temperature", 0.0));
    }

    @Test
    void temperature_aboveCriticalLow_returnsNearCritical() {
        assertEquals(SeverityLevel.NEAR_CRITICAL, classifyHelper.classifyMeasurement("temperature", -10.0));
    }

    @Test
    void temperature_atCriticalLow_returnsCritical(){
        assertEquals(SeverityLevel.CRITICAL, classifyHelper.classifyMeasurement("temperature", -20.0));
    }

    @Test
    void pressure_normal_returnsNormal() {
        assertEquals(SeverityLevel.NORMAL, classifyHelper.classifyMeasurement("pressure", 1000.0));
    }

    @Test
    void pressure_belowNearCriticalHigh_returnsNormal(){
        assertEquals(SeverityLevel.NORMAL, classifyHelper.classifyMeasurement("pressure", 1059.0));
    }

    @Test
    void pressure_atNearCriticalHigh_returnsNearCritical(){
        assertEquals(SeverityLevel.NEAR_CRITICAL, classifyHelper.classifyMeasurement("pressure", 1060.0));
    }

    @Test
    void pressure_belowCriticalHigh_returnsNearCritical() {
        assertEquals(SeverityLevel.NEAR_CRITICAL, classifyHelper.classifyMeasurement("pressure", 1080.0));
    }

    @Test
    void pressure_atCriticalHigh_returnsCritical() {
        assertEquals(SeverityLevel.CRITICAL, classifyHelper.classifyMeasurement("pressure", 1100.0));
    }

    @Test
    void pressure_aboveNearCriticalLow_returnsNormal(){
        assertEquals(SeverityLevel.NORMAL, classifyHelper.classifyMeasurement("pressure", 951.0));
    }

    @Test
    void pressure_atNearCriticalLow_returnsNearCritical(){
        assertEquals(SeverityLevel.NEAR_CRITICAL, classifyHelper.classifyMeasurement("pressure", 950.0));
    }

    @Test
    void pressure_aboveCriticalLow_returnsNearCritical() {
        assertEquals(SeverityLevel.NEAR_CRITICAL, classifyHelper.classifyMeasurement("pressure", 920.0));
    }

    @Test
    void pressure_atCriticalLow_returnsCritical(){
        assertEquals(SeverityLevel.CRITICAL, classifyHelper.classifyMeasurement("pressure", 900.0));
    }

    @Test
    void unknownKey_returnsNearCritical(){
        assertEquals(SeverityLevel.NEAR_CRITICAL, classifyHelper.classifyMeasurement("gyroscope", 100.0));
    }

    @Test
    void nonNumericKey_returnsNearCritical(){
        assertEquals(SeverityLevel.NEAR_CRITICAL, classifyHelper.classifyMeasurement("battery", "help"));
    }

}
