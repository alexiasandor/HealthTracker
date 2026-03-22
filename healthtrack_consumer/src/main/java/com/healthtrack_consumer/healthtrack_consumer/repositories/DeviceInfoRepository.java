package com.healthtrack_consumer.healthtrack_consumer.repositories;

import com.healthtrack_consumer.healthtrack_consumer.entities.DeviceInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface DeviceInfoRepository extends JpaRepository<DeviceInfo, UUID> {
}
