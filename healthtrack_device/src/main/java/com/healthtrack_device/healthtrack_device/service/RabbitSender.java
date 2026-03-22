package com.healthtrack_device.healthtrack_device.service;

import com.healthtrack_device.healthtrack_device.dtos.DeviceInfoDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RabbitSender {
    private final RabbitTemplate rabbitTemplate;

    public RabbitSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendCreateUpdateDevice(DeviceInfoDTO deviceInfoDTO) {
        rabbitTemplate.convertAndSend("device_information_c_u", deviceInfoDTO);
    }

    public void sendDeleteDevice(UUID device_id) {
        rabbitTemplate.convertAndSend("device_information_d", device_id);
    }
}
