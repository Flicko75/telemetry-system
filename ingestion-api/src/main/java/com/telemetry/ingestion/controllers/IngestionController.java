package com.telemetry.ingestion.controllers;

import com.telemetry.common.models.TelemetryPacket;
import com.telemetry.ingestion.services.IngestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class IngestionController {

    private final IngestionService ingestionService;

    @PostMapping("/telemetry")
    public ResponseEntity<String> receivePacket(@Valid @RequestBody TelemetryPacket packet){
        return ingestionService.receivePacket(packet);
    }

}
