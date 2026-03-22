package com.healthtrack_consumer.healthtrack_consumer.services;

import com.healthtrack_consumer.healthtrack_consumer.controllers.handlers.ResourceNotFoundException;
import com.healthtrack_consumer.healthtrack_consumer.dtos.DeviceConsumptionDTO;
import com.healthtrack_consumer.healthtrack_consumer.dtos.DeviceInfoDTO;
import com.healthtrack_consumer.healthtrack_consumer.dtos.DeviceInfoRequestDTO;
import com.healthtrack_consumer.healthtrack_consumer.dtos.MeasurementRequestDTO;
import com.healthtrack_consumer.healthtrack_consumer.entities.DeviceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RabbitConsumer {
    private final DeviceInfoService deviceInfoService;
    private final DeviceConsumptionService deviceConsumptionService;
    private final SimpMessagingTemplate messagingTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitConsumer.class);
    private double consumptionOnLastHour;

    @Autowired
    public RabbitConsumer(DeviceInfoService deviceInfoService, DeviceConsumptionService deviceConsumptionService, SimpMessagingTemplate messagingTemplate) {
        this.deviceInfoService = deviceInfoService;
        this.deviceConsumptionService = deviceConsumptionService;
        this.messagingTemplate = messagingTemplate;
        consumptionOnLastHour = 0.0d;
    }

    private boolean isHourlyConsumptionExceeded(List<DeviceConsumptionDTO> deviceConsumptionDTOList, double maxEnergyConsumption, double currentConsumption, Time time) {
        double lastHourConsumption = 0.0d;
        int currentHour = time.toLocalTime().getHour();

        if (currentHour > 0) {
            for (DeviceConsumptionDTO deviceConsumptionDTO : deviceConsumptionDTOList) {
                if (deviceConsumptionDTO.getTime().toLocalTime().getHour() == (currentHour - 1) && deviceConsumptionDTO.getEnergyConsumed() > lastHourConsumption) {
                    lastHourConsumption = deviceConsumptionDTO.getEnergyConsumed();
                }
            }
        }

        if((currentConsumption - lastHourConsumption) > maxEnergyConsumption) {
            consumptionOnLastHour = lastHourConsumption;
            return true;
        }

        return false;
    }

    @RabbitListener(queues = "measurements")
    public void consumeMeasurements(MeasurementRequestDTO measurementInfo) {
        LOGGER.info(measurementInfo.toString());

        Date date = new Date(measurementInfo.getTimestamp());
        Time time = new Time(measurementInfo.getTimestamp());

        try {
            DeviceInfo deviceInfo = deviceInfoService.findByDeviceInfoId(measurementInfo.getDevice_id());
            DeviceConsumptionDTO deviceConsumptionDTO = new DeviceConsumptionDTO(0, date, time, measurementInfo.getMeasurement_value(), deviceInfo);
            deviceConsumptionService.saveDeviceConsumption(deviceConsumptionDTO);
            List<DeviceConsumptionDTO> deviceConsumptionDTOList = deviceConsumptionService.findDeviceConsumptionListByDeviceIdAndDate(measurementInfo.getDevice_id(), date);

            if(isHourlyConsumptionExceeded(deviceConsumptionDTOList, deviceInfo.getMaximum_hourly_energy_consumption(), measurementInfo.getMeasurement_value(), time)) {
                messagingTemplate.convertAndSend("/topic/notify",
                        "You have exceeded the hourly consumption for one of your devices!!!\nConsumption reached: "
                                + (measurementInfo.getMeasurement_value() - consumptionOnLastHour) + "\nMaximum consumption: "
                                + deviceInfo.getMaximum_hourly_energy_consumption() + "\nId:"
                                + deviceInfo.getDeviceId());
            }

            Thread.sleep(2000);
        } catch (ResourceNotFoundException | MessagingException | InterruptedException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @RabbitListener(queues = "device_information_c_u")
    public void consumeDeviceInfo(DeviceInfoRequestDTO deviceInfoRequestDTO) {
        DeviceInfoDTO deviceInfoDTO = new DeviceInfoDTO(deviceInfoRequestDTO.getDeviceId(), deviceInfoRequestDTO.getMaximum_hourly_energy_consumption(), new ArrayList<>());
        deviceInfoService.saveOrUpdateDeviceInfo(deviceInfoDTO);
    }

    @RabbitListener(queues = "device_information_d")
    public void consumeDeviceDelete(UUID device_id) {
        try {
            deviceInfoService.deleteDeviceInfoById(device_id);
        } catch (ResourceNotFoundException e) {
            LOGGER.error(e.getMessage());
        }
    }

}
