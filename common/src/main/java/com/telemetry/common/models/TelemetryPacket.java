package com.telemetry.common.models;

import com.telemetry.common.enums.DeviceHealth;
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

    @NotBlank
    private String deviceId;

    @NotNull
    @NotEmpty
    private Map<String, Object> measurements;

    @NotNull
    private DeviceHealth deviceHealth;

    @NotNull
    private LocalDateTime sendingTime;

    private LocalDateTime receivingTime;

    @Valid
    private Coordinates coordinates;
}
