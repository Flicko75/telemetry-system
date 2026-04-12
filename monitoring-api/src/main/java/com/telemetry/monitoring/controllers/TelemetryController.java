package com.telemetry.monitoring.controllers;

import com.telemetry.common.DTOs.TelemetryPacketResponse;
import com.telemetry.monitoring.entity.TelemetryPacketEntity;
import com.telemetry.monitoring.services.TelemetryQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@Tag(name = "Telemetry", description = "Telemetry packet querying")
@RequiredArgsConstructor
@RequestMapping("/api/v1/telemetry")
public class TelemetryController {

    private final TelemetryQueryService queryService;

    @Operation(summary = "Get all telemetry packets",
            description = "Returns a paginated list of all telemetry packets across all devices")
    @ApiResponse(responseCode = "200", description = "Packets retrieved successfully")
    @GetMapping
    public ResponseEntity<Page<TelemetryPacketResponse>> getAllPackets(Pageable pageable){
        return ResponseEntity.ok(queryService.getAllPackets(pageable));
    }

    @Operation(summary = "Get packets by device",
            description = "Returns a paginated list of telemetry packets for the specified device")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Packets retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    @GetMapping("/{deviceId}")
    public ResponseEntity<Page<TelemetryPacketResponse>> getPacketByDevice(@PathVariable String deviceId, Pageable pageable){
        return ResponseEntity.ok(queryService.getPacketByDevice(deviceId, pageable));
    }

    @Operation(summary = "Get packets by device within time range",
            description = "Returns a paginated list of telemetry packets for the specified device between the given timestamps")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Packets retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    @GetMapping("/{deviceId}/range")
    public ResponseEntity<Page<TelemetryPacketResponse>> getPacketBetweenRange(@PathVariable String deviceId,
                                                             @RequestParam LocalDateTime start,
                                                             @RequestParam LocalDateTime end,
                                                             Pageable pageable){
        return ResponseEntity.ok(queryService.getPacketByDeviceAndTimeRange(deviceId, start, end, pageable));
    }

}
