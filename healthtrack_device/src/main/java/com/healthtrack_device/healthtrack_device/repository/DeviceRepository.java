package com.healthtrack_device.healthtrack_device.repository;

import com.healthtrack_device.healthtrack_device.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface DeviceRepository extends JpaRepository<Device, UUID> {
    List<Device> findAllByUser_UserId(UUID userid);
}
