package com.healthtrack_consumer.healthtrack_consumer.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.sql.*;

@Entity
@Table(name = "device_consumption")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DeviceConsumption implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long entryNumber;
    @Column(name = "date", nullable = false)
    private Date date;
    @Column(name = "time", nullable = false)
    private Time time;
    @Column(name = "energy_consumed", nullable = false)
    private double energyConsumed;
    @ManyToOne
    @JoinColumn(name = "device_id")
    private DeviceInfo device;
}
