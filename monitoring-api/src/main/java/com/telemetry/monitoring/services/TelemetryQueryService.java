package com.telemetry.monitoring.services;

import com.telemetry.common.exceptions.ResourceNotFoundException;
import com.telemetry.monitoring.entity.DeviceEntity;
import com.telemetry.monitoring.entity.TelemetryPacketEntity;
import com.telemetry.monitoring.repos.DeviceRepository;
import com.telemetry.monitoring.repos.TelemetryPacketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TelemetryQueryService {

    private final TelemetryPacketRepository packetRepository;

    private final DeviceRepository deviceRepository;

    public Page<TelemetryPacketEntity> getAllPackets(Pageable pageable){
        return packetRepository.findAll(pageable);
    }

    public Page<TelemetryPacketEntity> getPacketByDevice(String deviceId, Pageable pageable){
        DeviceEntity device = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));

        return packetRepository.findByDevice(device, pageable);
    }

    public Page<TelemetryPacketEntity> getPacketByDeviceAndTimeRange(String deviceId, LocalDateTime start, LocalDateTime end, Pageable pageable){
        DeviceEntity device = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));

        return packetRepository.findByDeviceAndReceivingTimeBetween(device, start, end, pageable);
    }

}
