package com.telemetry.monitoring.controllers;

import com.telemetry.monitoring.entity.TelemetryPacketEntity;
import com.telemetry.monitoring.services.TelemetryQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/telemetry")
public class TelemetryController {

    private final TelemetryQueryService queryService;

    @GetMapping
    public ResponseEntity<Page<TelemetryPacketEntity>> getAllPackets(Pageable pageable){
        return ResponseEntity.ok(queryService.getAllPackets(pageable));
    }

    @GetMapping("/{deviceId}")
    public ResponseEntity<Page<TelemetryPacketEntity>> getPacketByDevice(@PathVariable String deviceId, Pageable pageable){
        return ResponseEntity.ok(queryService.getPacketByDevice(deviceId, pageable));
    }

    @GetMapping("/{deviceId}/range")
    public ResponseEntity<Page<TelemetryPacketEntity>> getPacketBetweenRange(@PathVariable String deviceId,
                                                             @RequestParam LocalDateTime start,
                                                             @RequestParam LocalDateTime end,
                                                             Pageable pageable){
        return ResponseEntity.ok(queryService.getPacketByDeviceAndTimeRange(deviceId, start, end, pageable));
    }

}
