package com.healthtrack_device.healthtrack_device.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "device_t")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Device implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID deviceId;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "address", nullable = false)
    private String address;
    @Column(name = "maximum_hourly_energy_consumption", nullable = false)
    private double maximumHourlyEnergyConsumption;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserMapping user;
}
