package com.healthtrack_consumer.healthtrack_consumer.dtos.builders;

import com.healthtrack_consumer.healthtrack_consumer.dtos.DeviceConsumptionDTO;
import com.healthtrack_consumer.healthtrack_consumer.entities.DeviceConsumption;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DeviceConsumptionBuilder {
    public static DeviceConsumptionDTO mapToDeviceConsumptionDTO(DeviceConsumption deviceConsumption) {
        return DeviceConsumptionDTO.builder()
                .entryNumber(deviceConsumption.getEntryNumber())
                .date(deviceConsumption.getDate())
                .time(deviceConsumption.getTime())
                .energyConsumed(deviceConsumption.getEnergyConsumed())
                .device(deviceConsumption.getDevice())
                .build();
    }

    public static DeviceConsumption mapToDeviceConsumptionEntity(DeviceConsumptionDTO deviceConsumptionDTO) {
        return DeviceConsumption.builder()
                .entryNumber(deviceConsumptionDTO.getEntryNumber())
                .date(deviceConsumptionDTO.getDate())
                .time(deviceConsumptionDTO.getTime())
                .energyConsumed(deviceConsumptionDTO.getEnergyConsumed())
                .device(deviceConsumptionDTO.getDevice())
                .build();
    }
}
