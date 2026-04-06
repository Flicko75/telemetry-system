package com.telemetry.common.models;

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

    @NotNull
    @DecimalMin("-90")
    @DecimalMax("90")
    private double latitude;

    @NotNull
    @DecimalMin("-180")
    @DecimalMax("180")
    private double longitude;
}
