package com.telemetry.processing.entity;

import com.telemetry.common.enums.DeviceHealth;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "devices")
public class DeviceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String deviceId;

    private String deviceDesc;

    @Enumerated(EnumType.STRING)
    private DeviceHealth deviceHealth;

    private LocalDateTime lastSeen;

    private LocalDateTime registeredAt;

    private LocalDateTime officiallyRegisteredAt;

}
