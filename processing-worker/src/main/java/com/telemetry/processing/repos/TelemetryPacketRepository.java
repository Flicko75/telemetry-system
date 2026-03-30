package com.telemetry.processing.repos;

import com.telemetry.processing.entity.TelemetryPacketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelemetryPacketRepository extends JpaRepository<TelemetryPacketEntity, Long> {
}
