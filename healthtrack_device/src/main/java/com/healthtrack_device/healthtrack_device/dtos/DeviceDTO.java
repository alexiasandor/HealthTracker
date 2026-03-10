package com.healthtrack_device.healthtrack_device.dtos;

import com.healthtrack_device.healthtrack_device.entity.UserMapping;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

public class DeviceDTO {
    private UUID deviceId;
    private String description;
    private String address;
    private double maximumHourlyEnergyConsumption;
    private UserMapping user;
}
