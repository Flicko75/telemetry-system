package com.telemetry.monitoring.controllers;

import com.telemetry.common.DTOs.DeviceResponse;
import com.telemetry.common.DTOs.DeviceUpdateDTO;
import com.telemetry.monitoring.entity.DeviceEntity;
import com.telemetry.monitoring.services.DeviceLiveService;
import com.telemetry.monitoring.services.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@Tag(name = "Devices", description = "Device registration and live telemetry streaming")
@RequiredArgsConstructor
@RequestMapping("/api/v1/devices")
public class DeviceController {

    private final DeviceLiveService deviceLiveService;

    private final DeviceService deviceService;

    @Operation(summary = "Stream live device telemetry",
            description = "Opens an SSE stream that pushes live telemetry updates for the specified device")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SSE stream opened successfully"),
            @ApiResponse(responseCode = "404", description = "Device not found or no telemetry packets available")
    })
    @GetMapping("/{deviceId}/live")
    public SseEmitter subscribe(@PathVariable String deviceId){
        return deviceLiveService.subscribe(deviceId);
    }

    @Operation(summary = "Officially register a device",
            description = "Sets the official registration details for an already auto-registered device")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Device updated successfully"),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    @PatchMapping("/{deviceId}")
    public ResponseEntity<DeviceResponse> officiallyRegisterDevice(@PathVariable String deviceId,
                                                                   @RequestBody DeviceUpdateDTO updateDTO){
        return ResponseEntity.ok(deviceService.officiallyRegisterDevice(deviceId, updateDTO));
    }

}
