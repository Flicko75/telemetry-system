package com.simulator.state;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeviceState {

    private String deviceId;
    private double battery;
    private double temperature;
    private double pressure;
    private boolean charging;

}
