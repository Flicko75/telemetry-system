package com.telemetry.monitoring.repos;

import com.telemetry.monitoring.entity.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceRepository extends JpaRepository<DeviceEntity, Long> {

    Optional<DeviceEntity> findByDeviceId(String id);

}
