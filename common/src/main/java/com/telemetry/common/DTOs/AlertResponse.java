package com.telemetry.common.DTOs;

import com.telemetry.common.enums.AlertStatus;
import com.telemetry.common.enums.SeverityLevel;

import java.time.LocalDateTime;

public record AlertResponse(
        String deviceId,
        SeverityLevel severityLevel,
        AlertStatus alertStatus,
        LocalDateTime createdAt,
        LocalDateTime resolvedAt
) {}
