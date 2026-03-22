package com.healthtrack_consumer.healthtrack_consumer.dtos.builders;

import com.healthtrack_consumer.healthtrack_consumer.dtos.DeviceInfoDTO;
import com.healthtrack_consumer.healthtrack_consumer.entities.DeviceInfo;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DeviceInfoBuilder {
    public static DeviceInfoDTO mapToDeviceInfoDTO(DeviceInfo deviceInfo) {
        return DeviceInfoDTO.builder()
                .deviceId(deviceInfo.getDeviceId())
                .maximum_hourly_energy_consumption(deviceInfo.getMaximum_hourly_energy_consumption())
                .deviceConsumptionList(deviceInfo.getDeviceConsumptionList())
                .build();
    }

    public static DeviceInfo mapToDeviceInfoEntity(DeviceInfoDTO deviceInfoDTO) {
        return DeviceInfo.builder()
                .deviceId(deviceInfoDTO.getDeviceId())
                .maximum_hourly_energy_consumption(deviceInfoDTO.getMaximum_hourly_energy_consumption())
                .deviceConsumptionList(deviceInfoDTO.getDeviceConsumptionList())
                .build();
    }
}
