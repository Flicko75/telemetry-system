package com.telemetry.common.DTOs;

import java.time.LocalDateTime;

public record DeviceUpdateDTO(
        String deviceDesc,
        LocalDateTime officiallyRegisteredAt
) {}
