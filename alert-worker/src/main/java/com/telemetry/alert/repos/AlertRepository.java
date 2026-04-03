package com.telemetry.alert.repos;

import com.telemetry.alert.entity.AlertEntity;
import com.telemetry.alert.entity.DeviceEntity;
import com.telemetry.common.enums.AlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlertRepository extends JpaRepository<AlertEntity, Long> {

    Optional<AlertEntity> findByDeviceAndAlertStatus(DeviceEntity device, AlertStatus status);

}
