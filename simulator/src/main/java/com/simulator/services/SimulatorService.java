package com.simulator.services;

import com.simulator.state.DeviceState;
import com.telemetry.common.enums.DeviceHealth;
import com.telemetry.common.models.Coordinates;
import com.telemetry.common.models.TelemetryPacket;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SimulatorService {

    private final Map<String, DeviceState> devices = new HashMap<>();

    private final RestTemplate restTemplate;

    @PostConstruct
    public void init(){
        devices.put("SAT-001", new DeviceState("SAT-001", 100, 25, 1013, false));
        devices.put("SAT-002", new DeviceState("SAT-002", 80, 20, 1010, false));
        devices.put("SAT-003", new DeviceState("SAT-003", 60, 30, 1005, false));
    }

    @Scheduled(fixedRate = 3000)
    public void simulate(){
        devices.forEach((id, state) -> {
            if (state.isCharging()){
                state.setBattery(Math.min(100, state.getBattery() + 5));
                if (state.getBattery() >= 100)
                    state.setCharging(false);
            } else {
                state.setBattery(Math.max(15, state.getBattery() - 3));
                if (state.getBattery() <= 15)
                    state.setCharging(true);
            }

            state.setTemperature(state.getTemperature() + Math.random() * 4 - 2);

            state.setPressure(state.getPressure() + Math.random() * 6 - 3);

            Map<String, Object> measurements = new HashMap<>();
            measurements.put("battery", state.getBattery());
            measurements.put("temperature", state.getTemperature());
            measurements.put("pressure", state.getPressure());

            TelemetryPacket packet = new TelemetryPacket();
            packet.setDeviceId(id);
            packet.setDeviceHealth(DeviceHealth.HEALTHY);
            packet.setSendingTime(LocalDateTime.now());
            packet.setCoordinates(new Coordinates(10, 11));
            packet.setMeasurements(measurements);

            restTemplate.postForEntity(
                    "http://localhost:8081/api/v1/telemetry",
                    packet,
                    String.class);
        });
    }

}
