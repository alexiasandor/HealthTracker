package com.healthtrack_device.healthtrack_device.dtos.builders;

import com.healthtrack_device.healthtrack_device.dtos.UserMappingDetailsDTO;
import com.healthtrack_device.healthtrack_device.entity.UserMapping;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserMappingBuilder {
    public static UserMappingDetailsDTO mapToUserMappingDetailsDTO(UserMapping userMapping) {
        return UserMappingDetailsDTO.builder()
                .userId(userMapping.getUserId())
                .devices(userMapping.getDevices())
                .build();
    }

    public static UserMapping mapToUserMappingEntity(UserMappingDetailsDTO userMappingDetailsDTO) {
        return UserMapping.builder()
                .userId(userMappingDetailsDTO.getUserId())
                .devices(userMappingDetailsDTO.getDevices())
                .build();
    }
}
