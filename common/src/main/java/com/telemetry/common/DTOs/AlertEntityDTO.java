package com.telemetry.common.DTOs;

import com.telemetry.common.enums.AlertStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record AlertEntityDTO(
        @Schema(description = "New status to set for the alert", example = "RESOLVED")
        AlertStatus status
) {}
