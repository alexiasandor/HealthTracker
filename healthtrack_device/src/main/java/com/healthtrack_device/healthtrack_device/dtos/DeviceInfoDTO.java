package com.healthtrack_device.healthtrack_device.dtos;

import lombok.*;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DeviceInfoDTO {
    private UUID deviceId;
    private double maximum_hourly_energy_consumption;
}
