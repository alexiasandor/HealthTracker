package com.healthtrack_consumer.healthtrack_consumer.dtos;
import com.healthtrack_consumer.healthtrack_consumer.entities.DeviceConsumption;
import lombok.*;

import java.util.List;
import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DeviceInfoDTO {
    private UUID deviceId;
    private double maximum_hourly_energy_consumption;
    private List<DeviceConsumption> deviceConsumptionList;
}
