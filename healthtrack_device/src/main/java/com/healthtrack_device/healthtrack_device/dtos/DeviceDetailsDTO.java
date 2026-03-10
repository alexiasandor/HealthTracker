package com.healthtrack_device.healthtrack_device.dtos;

import com.healthtrack_device.healthtrack_device.entity.UserMapping;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

public class DeviceDetailsDTO {
    private UUID deviceId;
    @NotNull
    private String description;
    @NotNull
    private String address;
    @NotNull
    private double maximumHourlyEnergyConsumption;
    private UserMapping user;
}
