package com.healthtrack_consumer.healthtrack_consumer.repositories;

import com.healthtrack_consumer.healthtrack_consumer.entities.DeviceConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;
@Repository
public interface DeviceConsumptionRepository extends JpaRepository<DeviceConsumption, UUID> {
    List<DeviceConsumption> findAllByDevice_DeviceIdAndDate(UUID deviceConsumptionId, Date date);
    List<DeviceConsumption> findAllByDate(Date date);
    boolean existsByDevice_DeviceId(UUID deviceId);
}
