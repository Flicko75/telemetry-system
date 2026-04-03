package com.telemetry.monitoring.services;

import com.telemetry.common.DTOs.AlertEntityDTO;
import com.telemetry.monitoring.entity.AlertEntity;
import com.telemetry.monitoring.entity.DeviceEntity;
import com.telemetry.monitoring.repos.AlertRepository;
import com.telemetry.monitoring.repos.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;

    private final DeviceRepository deviceRepository;

    public List<AlertEntity> getAlertsByDevice(String deviceId){
        DeviceEntity device = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        return alertRepository.findByDevice(device);
    }

    public AlertEntity resolveAlert(Long alertId, AlertEntityDTO dto){
        AlertEntity alertEntity = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));

        alertEntity.setAlertStatus(dto.status());
        alertEntity.setResolvedAt(LocalDateTime.now());

        return alertRepository.save(alertEntity);
    }

}
