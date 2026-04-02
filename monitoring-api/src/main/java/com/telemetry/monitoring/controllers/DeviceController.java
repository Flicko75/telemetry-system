package com.telemetry.monitoring.controllers;

import com.telemetry.monitoring.services.DeviceLiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/devices")
public class DeviceController {

    private final DeviceLiveService deviceLiveService;

    @GetMapping("/{deviceId}/live")
    public SseEmitter subscribe(@PathVariable String deviceId){
        return deviceLiveService.subscribe(deviceId);
    }

}
