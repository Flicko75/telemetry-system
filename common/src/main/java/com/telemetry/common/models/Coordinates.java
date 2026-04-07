package com.telemetry.common.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coordinates {

    @Schema(description = "Latitude of the device", example = "40.7128")
    @NotNull
    @DecimalMin("-90")
    @DecimalMax("90")
    private double latitude;

    @Schema(description = "Longitude of the device", example = "-74.0060")
    @NotNull
    @DecimalMin("-180")
    @DecimalMax("180")
    private double longitude;
}
