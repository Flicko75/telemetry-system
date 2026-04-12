package com.telemetry.common.DTOs;

import com.telemetry.common.enums.DeviceHealth;

import java.time.LocalDateTime;

public record DeviceResponse(
        String deviceId,
        String deviceDesc,
        DeviceHealth deviceHealth,
        LocalDateTime lastSeen,
        LocalDateTime registeredAt,
        LocalDateTime officiallyRegisteredAt
) {}
