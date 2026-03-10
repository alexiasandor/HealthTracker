package com.healthtrack_device.healthtrack_device.service;

import com.healthtrack_device.healthtrack_device.controllers.handlers.ResourceNotFoundException;
import com.healthtrack_device.healthtrack_device.dtos.DeviceDTO;
import com.healthtrack_device.healthtrack_device.dtos.DeviceDetailsDTO;
import com.healthtrack_device.healthtrack_device.dtos.builders.DeviceBuilder;
import com.healthtrack_device.healthtrack_device.entity.Device;
import com.healthtrack_device.healthtrack_device.repository.DeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeviceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);
    private final DeviceRepository deviceRepository;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Transactional
    public List<DeviceDTO> findAllDevices() {
        List<Device> deviceList = deviceRepository.findAll();
        List<DeviceDTO> deviceDTOList = new ArrayList<>();

        deviceList.forEach(device -> deviceDTOList.add(DeviceBuilder.mapToDeviceDTO(device)));

        LOGGER.debug("List of all devices received!");

        return deviceDTOList;
    }

    @Transactional
    public DeviceDetailsDTO findDeviceById(UUID deviceId) throws ResourceNotFoundException {
        Optional<Device> optionalDevice = deviceRepository.findById(deviceId);

        if(optionalDevice.isEmpty()) {
            LOGGER.error("Device with id \"{}\" was not found!", deviceId);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " device with id: " + deviceId);
        }

        LOGGER.debug("Device received!");

        return DeviceBuilder.mapToDeviceDetailsDTO(optionalDevice.get());
    }
    @Transactional
    public List<DeviceDTO> findDevicesByUserId(UUID userId) {
        List<Device> deviceList = deviceRepository.findAllByUser_UserId(userId);
        List<DeviceDTO> deviceDTOList = new ArrayList<>();

        deviceList.forEach(device -> deviceDTOList.add(DeviceBuilder.mapToDeviceDTO(device)));

        LOGGER.debug("List of all devices with user id \"{}\" received!", userId);

        return deviceDTOList;
    }

    public void updateDevicesByUserId(UUID userId) {
        List<DeviceDTO> deviceDTOList = findDevicesByUserId(userId);
        for(DeviceDTO deviceDTO: deviceDTOList) {
            DeviceDetailsDTO deviceDetailsDTO = DeviceBuilder.mapToDeviceDetailsDTO(deviceDTO);
            deviceDetailsDTO.setUser(null);
            updateDeviceById(deviceDetailsDTO, deviceDetailsDTO.getDeviceId());
        }
    }

    @Transactional
    public UUID saveDevice(DeviceDetailsDTO deviceDetailsDTO) {
        Device device = DeviceBuilder.mapToDeviceEntity(deviceDetailsDTO);

        device = deviceRepository.save(device);
        LOGGER.debug("Device with id \"{}\" was inserted in db!", device.getDeviceId());

        return device.getDeviceId();
    }

    @Transactional
    public DeviceDetailsDTO updateDeviceById(DeviceDetailsDTO deviceDetailsDTO, UUID deviceId) throws ResourceNotFoundException {
        Optional<Device> optionalDevice = deviceRepository.findById(deviceId);

        if(optionalDevice.isEmpty()) {
            LOGGER.error("Device with id \"{}\" was not found!", deviceId);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " device with id: " + deviceId);
        }

        Device updatedDevice = optionalDevice.map(device -> {
            device.setDeviceId(deviceDetailsDTO.getDeviceId());
            device.setAddress(deviceDetailsDTO.getAddress());
            device.setDescription(deviceDetailsDTO.getDescription());
            device.setMaximumHourlyEnergyConsumption(deviceDetailsDTO.getMaximumHourlyEnergyConsumption());
            device.setUser(deviceDetailsDTO.getUser());

            return deviceRepository.save(device);
        }).get();

        LOGGER.debug("Device with id \"{}\" was updated in db!", updatedDevice.getDeviceId());

        return DeviceBuilder.mapToDeviceDetailsDTO(updatedDevice);
    }

    @Transactional
    public String deleteDeviceById(UUID deviceId) throws ResourceNotFoundException {
        Optional<Device> optionalDevice = deviceRepository.findById(deviceId);

        if(optionalDevice.isEmpty()) {
            LOGGER.error("Device with id \"{}\" was not found!", deviceId);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " device with id: " + deviceId);
        }

        deviceRepository.deleteById(deviceId);
        LOGGER.debug("Device with id \"{}\" was deleted from the db!", optionalDevice.get().getDeviceId());

        return "Device with id " + deviceId + " was deleted from the db!";
    }
}
