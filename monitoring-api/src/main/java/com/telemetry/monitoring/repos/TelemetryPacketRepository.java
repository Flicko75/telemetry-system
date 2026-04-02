package com.telemetry.monitoring.repos;

import com.telemetry.monitoring.entity.DeviceEntity;
import com.telemetry.monitoring.entity.TelemetryPacketEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TelemetryPacketRepository extends JpaRepository<TelemetryPacketEntity, Long> {

    Optional<TelemetryPacketEntity> findByDevice(DeviceEntity device);

    Page<TelemetryPacketEntity> findByDevice(DeviceEntity device, Pageable pageable);

    Optional<TelemetryPacketEntity> findByDeviceAndReceivingTimeBetween(DeviceEntity device, LocalDateTime start, LocalDateTime end);

    Page<TelemetryPacketEntity> findByDeviceAndReceivingTimeBetween(DeviceEntity device, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Optional<TelemetryPacketEntity> findTopByDeviceOrderByReceivingTimeDesc(DeviceEntity device);

}
