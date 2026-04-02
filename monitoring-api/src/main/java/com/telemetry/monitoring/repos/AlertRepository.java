package com.telemetry.monitoring.repos;

import com.telemetry.common.enums.AlertStatus;
import com.telemetry.monitoring.entity.AlertEntity;
import com.telemetry.monitoring.entity.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<AlertEntity, Long> {

    List<AlertEntity> findByDevice(DeviceEntity device);

    List<AlertEntity> findByAlertStatus(AlertStatus status);

}
