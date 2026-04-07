package com.telemetry.common.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record DeviceUpdateDTO(
        @Schema(description = "Human readable description of the device",
                example = "Satellite sensor unit deployed in sector 4")
        String deviceDesc,

        @Schema(description = "Official registration timestamp, defaults to now if not provided",
                example = "2024-01-15T10:30:00")
        LocalDateTime officiallyRegisteredAt
) {}
