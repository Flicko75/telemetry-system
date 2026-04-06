package com.telemetry.processing.services;

import com.telemetry.common.enums.SeverityLevel;
import com.telemetry.common.models.TelemetryPacket;
import com.telemetry.processing.entity.DeviceEntity;
import com.telemetry.processing.entity.TelemetryPacketEntity;
import com.telemetry.processing.repos.DeviceRepository;
import com.telemetry.processing.repos.TelemetryPacketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelemetryPersistenceService {

    private final DeviceRepository deviceRepository;

    private final TelemetryPacketRepository telemetryPacketRepository;

    public void persist(TelemetryPacket packet, SeverityLevel severityLevel){
        DeviceEntity device = deviceRepository.findByDeviceId(packet.getDeviceId())
                .orElseGet(() -> {
                    DeviceEntity deviceEntity = new DeviceEntity();
                    deviceEntity.setDeviceId(packet.getDeviceId());
                    deviceEntity.setDeviceDesc("Auto registered device");
                    deviceEntity.setRegisteredAt(LocalDateTime.now());

                    log.info("Auto registered new device {}", packet.getDeviceId());
                    return deviceEntity;
                });

        device.setDeviceHealth(packet.getDeviceHealth());
        device.setLastSeen(packet.getReceivingTime());

        deviceRepository.save(device);

        TelemetryPacketEntity telemetryPacket = new TelemetryPacketEntity();
        telemetryPacket.setDevice(device);
        telemetryPacket.setMeasurements(packet.getMeasurements());
        telemetryPacket.setSendingTime(packet.getSendingTime());
        telemetryPacket.setReceivingTime(packet.getReceivingTime());
        telemetryPacket.setLatitude(packet.getCoordinates().getLatitude());
        telemetryPacket.setLongitude(packet.getCoordinates().getLongitude());
        telemetryPacket.setSeverityLevel(severityLevel);

        log.info("Packet persisted for device {} with severity {}", packet.getDeviceId(), severityLevel);
        telemetryPacketRepository.save(telemetryPacket);
    }

}
