package com.telemetry.processing.entity;

import com.telemetry.common.enums.SeverityLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "telemetry_packets")
public class TelemetryPacketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private DeviceEntity device;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> measurements;

    private LocalDateTime sendingTime;

    private LocalDateTime receivingTime;

    private Double latitude;

    private Double longitude;

    @Enumerated(EnumType.STRING)
    private SeverityLevel severityLevel;

}
