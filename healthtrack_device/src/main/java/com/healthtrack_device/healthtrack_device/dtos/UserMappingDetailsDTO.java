package com.healthtrack_device.healthtrack_device.dtos;

import com.healthtrack_device.healthtrack_device.entity.Device;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserMappingDetailsDTO {
    @NotNull
    private UUID userId;
    private List<Device> devices;
}
