package com.telemetry.ingestion.controllers;

import com.telemetry.common.models.TelemetryPacket;
import com.telemetry.ingestion.services.IngestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Ingestion", description = "Handles incoming telemetry packets from field devices")
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class IngestionController {

    private final IngestionService ingestionService;

    @Operation(
            summary = "Submit telemetry packet",
            description = "Accepts a telemetry packet from device, applies rate limiting and forwards it to Kafka for processing"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Packet accepted and forwarded to Kafka"),
            @ApiResponse(responseCode = "400", description = "Validation failed. Missing or Invalid fields"),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded. Max 25 requests per minute per device")
    })
    @PostMapping("/telemetry")
    public ResponseEntity<String> receivePacket(@Valid @RequestBody TelemetryPacket packet){
        return ingestionService.receivePacket(packet);
    }

}
