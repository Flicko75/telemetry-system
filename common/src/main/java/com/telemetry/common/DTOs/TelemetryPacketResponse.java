package com.telemetry.common.DTOs;

import java.time.LocalDateTime;
import java.util.Map;

public record TelemetryPacketResponse(
        String deviceId,
        Map<String, Object> measurements,
        LocalDateTime sendingTime,
        LocalDateTime receivingTime,
        Double latitude,
        Double longitude
) {}
