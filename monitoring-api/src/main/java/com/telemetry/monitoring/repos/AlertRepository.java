package com.telemetry.monitoring.repos;

import com.telemetry.common.enums.AlertStatus;
import com.telemetry.monitoring.entity.AlertEntity;
import com.telemetry.monitoring.entity.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlertRepository extends JpaRepository<AlertEntity, Long> {

    Optional<AlertEntity> findByDevice(DeviceEntity device);

    Optional<AlertEntity> findByAlertStatus(AlertStatus status);

}
