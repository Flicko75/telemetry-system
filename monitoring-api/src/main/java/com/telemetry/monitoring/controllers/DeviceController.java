package com.telemetry.monitoring.controllers;

import com.telemetry.common.DTOs.DeviceUpdateDTO;
import com.telemetry.monitoring.entity.DeviceEntity;
import com.telemetry.monitoring.services.DeviceLiveService;
import com.telemetry.monitoring.services.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/devices")
public class DeviceController {

    private final DeviceLiveService deviceLiveService;

    private final DeviceService deviceService;

    @GetMapping("/{deviceId}/live")
    public SseEmitter subscribe(@PathVariable String deviceId){
        return deviceLiveService.subscribe(deviceId);
    }

    @PatchMapping("/{deviceId}")
    public ResponseEntity<DeviceEntity> officiallyRegisterDevice(@PathVariable String deviceId,
                                                                 @RequestBody DeviceUpdateDTO updateDTO){
        return deviceService.officiallyRegisterDevice(deviceId, updateDTO);
    }

}
