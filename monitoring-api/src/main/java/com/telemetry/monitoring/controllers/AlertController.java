package com.telemetry.monitoring.controllers;

import com.telemetry.common.DTOs.AlertEntityDTO;
import com.telemetry.monitoring.entity.AlertEntity;
import com.telemetry.monitoring.services.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/alerts")
public class AlertController {

    private final AlertService alertService;

    @GetMapping("/{deviceId}")
    public ResponseEntity<List<AlertEntity>> getAlertsByDevice(@PathVariable String deviceId){
        return ResponseEntity.ok(alertService.getAlertsByDevice(deviceId));
    }

    @PatchMapping("/{alertId}")
    public ResponseEntity<AlertEntity> resolveAlert(@PathVariable Long alertId,
                                                    @RequestBody AlertEntityDTO dto){
        return ResponseEntity.ok(alertService.resolveAlert(alertId, dto));
    }

}
