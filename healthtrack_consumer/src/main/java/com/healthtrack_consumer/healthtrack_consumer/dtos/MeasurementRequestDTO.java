package com.healthtrack_consumer.healthtrack_consumer.dtos;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MeasurementRequestDTO {
    private long timestamp;
    private UUID device_id;
    private double measurement_value;

    @Override
    public String toString() {
        return "MeasurementRequestDTO{" +
                "timestamp=" + timestamp +
                ", device_id=" + device_id +
                ", measurement_value=" + measurement_value +
                '}';
    }

}
