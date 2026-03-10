package com.healthtrack_device.healthtrack_device.dtos.builders;

import com.healthtrack_device.healthtrack_device.dtos.DeviceDTO;
import com.healthtrack_device.healthtrack_device.dtos.DeviceDetailsDTO;
import com.healthtrack_device.healthtrack_device.entity.Device;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DeviceBuilder {
    public static DeviceDTO mapToDeviceDTO(Device device) {
        return DeviceDTO.builder()
                .deviceId(device.getDeviceId())
                .description(device.getDescription())
                .address(device.getAddress())
                .maximumHourlyEnergyConsumption(device.getMaximumHourlyEnergyConsumption())
                .user(device.getUser())
                .build();
    }

    public static DeviceDetailsDTO mapToDeviceDetailsDTO(Device device) {
        return DeviceDetailsDTO.builder()
                .deviceId(device.getDeviceId())
                .description(device.getDescription())
                .address(device.getAddress())
                .maximumHourlyEnergyConsumption(device.getMaximumHourlyEnergyConsumption())
                .user(device.getUser())
                .build();
    }

    public static DeviceDetailsDTO mapToDeviceDetailsDTO(DeviceDTO deviceDTO) {
        return DeviceDetailsDTO.builder()
                .deviceId(deviceDTO.getDeviceId())
                .description(deviceDTO.getDescription())
                .address(deviceDTO.getAddress())
                .maximumHourlyEnergyConsumption(deviceDTO.getMaximumHourlyEnergyConsumption())
                .user(deviceDTO.getUser())
                .build();
    }

    public static Device mapToDeviceEntity(DeviceDetailsDTO deviceDTO) {
        return Device.builder()
                .deviceId(deviceDTO.getDeviceId())
                .description(deviceDTO.getDescription())
                .address(deviceDTO.getAddress())
                .maximumHourlyEnergyConsumption(deviceDTO.getMaximumHourlyEnergyConsumption())
                .user(deviceDTO.getUser())
                .build();
    }
}
