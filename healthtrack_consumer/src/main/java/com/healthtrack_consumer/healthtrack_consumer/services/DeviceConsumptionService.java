package com.healthtrack_consumer.healthtrack_consumer.services;

import com.healthtrack_consumer.healthtrack_consumer.controllers.handlers.ResourceNotFoundException;
import com.healthtrack_consumer.healthtrack_consumer.dtos.DeviceConsumptionDTO;
import com.healthtrack_consumer.healthtrack_consumer.dtos.builders.DeviceConsumptionBuilder;
import com.healthtrack_consumer.healthtrack_consumer.entities.DeviceConsumption;
import com.healthtrack_consumer.healthtrack_consumer.repositories.DeviceConsumptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DeviceConsumptionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceConsumptionService.class);
    private final DeviceConsumptionRepository deviceConsumptionRepository;
    @Autowired
    public DeviceConsumptionService(DeviceConsumptionRepository deviceConsumptionRepository) {
        this.deviceConsumptionRepository = deviceConsumptionRepository;
    }

    @Transactional
    public void saveDeviceConsumption(DeviceConsumptionDTO deviceConsumptionDTO) {
        DeviceConsumption deviceConsumption = DeviceConsumptionBuilder.mapToDeviceConsumptionEntity(deviceConsumptionDTO);

        deviceConsumption = deviceConsumptionRepository.save(deviceConsumption);
        LOGGER.debug("Entry \"{}\" and with device id \"{}\" was inserted in db!", deviceConsumption.getEntryNumber(), deviceConsumption.getDevice().getDeviceId());
    }

    @Transactional
    public List<DeviceConsumptionDTO> findDeviceConsumptionListByDeviceIdAndDate(UUID deviceConsumptionId, Date date) {
        if(!deviceConsumptionRepository.existsByDevice_DeviceId(deviceConsumptionId)) {
            LOGGER.error("Device consumption with id \"{}\" was not found!", deviceConsumptionId);
            throw new ResourceNotFoundException(DeviceConsumption.class.getSimpleName() + " device with id: " + deviceConsumptionId);
        }

        List<DeviceConsumptionDTO> deviceConsumptionDTOList = new ArrayList<>();
        List<DeviceConsumption> deviceConsumptionList = deviceConsumptionRepository.findAllByDevice_DeviceIdAndDate(deviceConsumptionId, date);
        LOGGER.debug("List of consumptions of device with id \"{}\" and date \"{}\" found!", deviceConsumptionId, date);

        deviceConsumptionList.forEach(deviceConsumption -> deviceConsumptionDTOList.add(DeviceConsumptionBuilder.mapToDeviceConsumptionDTO(deviceConsumption)));

        return deviceConsumptionDTOList;
    }

    @Transactional
    public List<Double> findDeviceConsumptionsByDeviceIdAndDate(UUID deviceConsumptionId, Date date) {
        if(!deviceConsumptionRepository.existsByDevice_DeviceId(deviceConsumptionId)) {
            LOGGER.error("Device consumption with id \"{}\" was not found!", deviceConsumptionId);
            throw new ResourceNotFoundException(DeviceConsumption.class.getSimpleName() + " device with id: " + deviceConsumptionId);
        }

        List<DeviceConsumption> deviceConsumptionList = deviceConsumptionRepository.findAllByDevice_DeviceIdAndDate(deviceConsumptionId, date);
        LOGGER.debug("List of consumptions of device with id \"{}\" and date \"{}\" found!", deviceConsumptionId, date);

        List<Double> hourlyConsumption = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            hourlyConsumption.add(i, 0.0d);
        }

        for (DeviceConsumption deviceConsumption : deviceConsumptionList) {
            int hour = deviceConsumption.getTime().toLocalTime().getHour();
            Double newConsumption = deviceConsumption.getEnergyConsumed();

            if(newConsumption > hourlyConsumption.get(hour)) {
                hourlyConsumption.set(hour, newConsumption);
            }
        }

        for(int i = 1; i < 24; i++) {
            if(hourlyConsumption.get(i) >= hourlyConsumption.get(i - 1)) {
                hourlyConsumption.set(i, hourlyConsumption.get(i) - hourlyConsumption.get(i - 1));
            }
        }

        return hourlyConsumption;
    }
}
