package com.telemetry.monitoring.services;

import com.telemetry.common.DTOs.DeviceUpdateDTO;
import com.telemetry.common.exceptions.ResourceNotFoundException;
import com.telemetry.monitoring.entity.DeviceEntity;
import com.telemetry.monitoring.repos.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public ResponseEntity<DeviceEntity> officiallyRegisterDevice(String deviceId, DeviceUpdateDTO updateDTO) {
        DeviceEntity device = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));

        if (updateDTO.deviceDesc() != null){
            device.setDeviceDesc(updateDTO.deviceDesc());
        }
        device.setOfficiallyRegisteredAt(
                updateDTO.officiallyRegisteredAt() != null ? updateDTO.officiallyRegisteredAt() : LocalDateTime.now()
        );

        log.info("Device {} officially registered", deviceId);

        return ResponseEntity.ok(deviceRepository.save(device));
    }

}
