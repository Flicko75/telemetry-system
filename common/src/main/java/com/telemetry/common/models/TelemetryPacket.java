package com.telemetry.common.models;

import com.telemetry.common.enums.DeviceHealth;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TelemetryPacket {
    private String deviceId;

    private Map<String, Object> measurements;

    private DeviceHealth deviceHealth;

    private LocalDateTime sendingTime;

    private LocalDateTime receivingTime;

    private Coordinates coordinates;
}
