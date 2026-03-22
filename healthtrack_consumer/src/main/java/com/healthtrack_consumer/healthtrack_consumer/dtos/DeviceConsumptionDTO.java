package com.healthtrack_consumer.healthtrack_consumer.dtos;
import com.healthtrack_consumer.healthtrack_consumer.entities.DeviceInfo;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.sql.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DeviceConsumptionDTO extends RepresentationModel<DeviceConsumptionDTO> {
    private long entryNumber;
    private Date date;
    private Time time;
    private double energyConsumed;
    private DeviceInfo device;
}
