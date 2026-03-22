package com.healthtrack_consumer.healthtrack_consumer.services;

import com.healthtrack_consumer.healthtrack_consumer.controllers.handlers.ResourceNotFoundException;
import com.healthtrack_consumer.healthtrack_consumer.dtos.DeviceInfoDTO;
import com.healthtrack_consumer.healthtrack_consumer.dtos.builders.DeviceInfoBuilder;
import com.healthtrack_consumer.healthtrack_consumer.entities.DeviceInfo;
import com.healthtrack_consumer.healthtrack_consumer.repositories.DeviceInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class DeviceInfoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceInfoService.class);
    private final DeviceInfoRepository deviceInfoRepository;

    @Autowired
    public DeviceInfoService(DeviceInfoRepository deviceInfoRepository) {
        this.deviceInfoRepository = deviceInfoRepository;
    }

    @Transactional
    public DeviceInfo findByDeviceInfoId(UUID deviceInfoId) {
        Optional<DeviceInfo> optionalDeviceInfo = deviceInfoRepository.findById(deviceInfoId);

        if(optionalDeviceInfo.isEmpty()) {
            LOGGER.error("Device with id \"{}\" was not found!", deviceInfoId);
            throw new ResourceNotFoundException(DeviceInfo.class.getSimpleName() + " device with id: " + deviceInfoId);
        }

        return optionalDeviceInfo.get();
    }

    @Transactional
    public void saveOrUpdateDeviceInfo(DeviceInfoDTO deviceInfoDTO) {
        Optional<DeviceInfo> optionalDeviceInfo = deviceInfoRepository.findById(deviceInfoDTO.getDeviceId());
        DeviceInfo deviceInfo = DeviceInfoBuilder.mapToDeviceInfoEntity(deviceInfoDTO);

        if(optionalDeviceInfo.isPresent()) {
            deviceInfo.setDeviceConsumptionList(optionalDeviceInfo.get().getDeviceConsumptionList());
        }

        deviceInfo = deviceInfoRepository.save(deviceInfo);
        LOGGER.debug("Device with id \"{}\" was inserted/updated in db!", deviceInfo.getDeviceId());
    }

    @Transactional
    public void deleteDeviceInfoById(UUID deviceInfoId) {
        Optional<DeviceInfo> optionalDeviceInfo = deviceInfoRepository.findById(deviceInfoId);

        if(optionalDeviceInfo.isEmpty()) {
            LOGGER.error("Device with id \"{}\" was not found!", deviceInfoId);
            throw new ResourceNotFoundException(DeviceInfo.class.getSimpleName() + " device with id: " + deviceInfoId);
        }

        deviceInfoRepository.deleteById(deviceInfoId);
        LOGGER.debug("Device with id \"{}\" was deleted from the db!", optionalDeviceInfo.get().getDeviceId());
    }
}
