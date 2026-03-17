package com.healthtrack_device.healthtrack_device.service;

import com.healthtrack_device.healthtrack_device.controllers.handlers.ResourceNotFoundException;
import com.healthtrack_device.healthtrack_device.dtos.UserMappingDetailsDTO;
import com.healthtrack_device.healthtrack_device.dtos.builders.UserMappingBuilder;
import com.healthtrack_device.healthtrack_device.entity.UserMapping;
import com.healthtrack_device.healthtrack_device.repository.UserMappingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserMappingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserMappingService.class);
    private final UserMappingRepository userMappingRepository;

    public UserMappingService(UserMappingRepository userMappingRepository) {
        this.userMappingRepository = userMappingRepository;
    }

    @Transactional
    public UUID saveUserMapping(UserMappingDetailsDTO userMappingDetailsDTO) {
        UserMapping userMapping = UserMappingBuilder.mapToUserMappingEntity(userMappingDetailsDTO);

        userMapping = userMappingRepository.save(userMapping);
        LOGGER.debug("UserMapping with id \"{}\" was inserted in db!", userMapping.getUserId());

        return userMapping.getUserId();
    }

    @Transactional
    public String deleteUserMappingById(UUID userId) throws ResourceNotFoundException {
        Optional<UserMapping> optionalUserMapping = userMappingRepository.findById(userId);

        if(optionalUserMapping.isEmpty()) {
            LOGGER.error("UserMapping with id \"{}\" was not found!", userId);
            throw new ResourceNotFoundException(UserMapping.class.getSimpleName() + " user_mapping with id: " + userId);
        }

        userMappingRepository.deleteById(userId);
        LOGGER.debug("UserMapping with id \"{}\" was deleted from the db!", optionalUserMapping.get().getUserId());

        return "UserMapping with id \"" + userId + "\" was deleted from the db!";
    }
}
