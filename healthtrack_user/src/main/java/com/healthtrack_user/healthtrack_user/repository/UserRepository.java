package com.healthtrack_user.healthtrack_user.repository;

import com.healthtrack_user.healthtrack_user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUserEmailAndUserPassword(String email, String password);
    Optional<User> findByUserEmail(String email);
}
