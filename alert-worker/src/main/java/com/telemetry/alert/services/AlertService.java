package com.telemetry.alert.services;

import com.telemetry.alert.entity.AlertEntity;
import com.telemetry.alert.entity.DeviceEntity;
import com.telemetry.alert.repos.AlertRepository;
import com.telemetry.alert.repos.DeviceRepository;
import com.telemetry.common.enums.AlertStatus;
import com.telemetry.common.enums.SeverityLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final DeviceRepository deviceRepository;

    private final AlertRepository alertRepository;

    public void createAlertIfNeeded(String deviceId, SeverityLevel severityLevel){
        DeviceEntity device = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        Optional<AlertEntity> alert = alertRepository.findByDeviceAndAlertStatus(device, AlertStatus.ACTIVE);

        if (alert.isPresent()){
            return;
        }

        AlertEntity newAlert = new AlertEntity();
        newAlert.setDevice(device);
        newAlert.setAlertStatus(AlertStatus.ACTIVE);
        newAlert.setCreatedAt(LocalDateTime.now());
        newAlert.setSeverityLevel(severityLevel);
        alertRepository.save(newAlert);
    }

}
