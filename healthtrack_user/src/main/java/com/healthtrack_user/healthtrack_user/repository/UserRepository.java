package com.healthtrack_user.healthtrack_user.repository;

import com.healthtrack_user.healthtrack_user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

}
