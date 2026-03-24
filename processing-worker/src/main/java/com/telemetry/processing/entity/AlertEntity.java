package com.telemetry.processing.entity;

import com.telemetry.common.enums.AlertStatus;
import com.telemetry.common.enums.SeverityLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "alerts")
public class AlertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private DeviceEntity device;

    @Enumerated(EnumType.STRING)
    private SeverityLevel severityLevel;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private AlertStatus alertStatus;

    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;

}
