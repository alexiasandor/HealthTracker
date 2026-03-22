package com.healthtrack_consumer.healthtrack_consumer.dtos;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DeviceInfoRequestDTO {
    private UUID deviceId;
    private double maximum_hourly_energy_consumption;
}
