package com.healthtrack_device.healthtrack_device.repository;

import com.healthtrack_device.healthtrack_device.entity.UserMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserMappingRepository extends JpaRepository <UserMapping, UUID>{
}
