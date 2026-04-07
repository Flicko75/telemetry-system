package com.telemetry.common.models;

import com.telemetry.common.enums.DeviceHealth;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TelemetryPacket {

    @Schema(description = "Unique device identifier", example = "SAT-001")
    @NotBlank
    private String deviceId;

    @Schema(description = "Key-value pairs of device readings",
            example = "{\"battery\": 85, \"temperature\": 36.2, \"pressure\": 101.3}"
    )
    @NotNull
    @NotEmpty
    private Map<String, Object> measurements;

    @Schema(description = "Current health status of the device", example = "HEALTHY")
    @NotNull
    private DeviceHealth deviceHealth;

    @Schema(description = "Time the packet was sent by the device", example = "2024-01-15T10:30:00")
    @NotNull
    private LocalDateTime sendingTime;

    @Schema(description = "Time the packet was received by the ingestion API, set server-side")
    private LocalDateTime receivingTime;

    @Schema(description = "GPS coordinates of the device at time of sending")
    @Valid
    private Coordinates coordinates;
}
