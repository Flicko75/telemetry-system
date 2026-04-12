package com.telemetry.monitoring.controllers;

import com.telemetry.common.DTOs.AlertEntityDTO;
import com.telemetry.common.DTOs.AlertResponse;
import com.telemetry.monitoring.entity.AlertEntity;
import com.telemetry.monitoring.services.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Alerts", description = "Alert retrieval and resolution")
@RequiredArgsConstructor
@RequestMapping("/api/v1/alerts")
public class AlertController {

    private final AlertService alertService;


    @Operation(summary = "Get alerts by device",
            description = "Returns all alerts associated with the specified device"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alerts retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    @GetMapping("/{deviceId}")
    public ResponseEntity<List<AlertResponse>> getAlertsByDevice(@PathVariable String deviceId){
        return ResponseEntity.ok(alertService.getAlertsByDevice(deviceId));
    }

    @Operation(summary = "Resolve an alert",
            description = "Updates the status of an alert to resolved")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alert resolved successfully"),
            @ApiResponse(responseCode = "404", description = "Alert not found")
    })
    @PatchMapping("/{alertId}")
    public ResponseEntity<AlertResponse> resolveAlert(@PathVariable Long alertId,
                                                      @RequestBody AlertEntityDTO dto){
        return ResponseEntity.ok(alertService.resolveAlert(alertId, dto));
    }

}
