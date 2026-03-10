package com.healthtrack_device.healthtrack_device.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;
@Entity
@Table(name = "user_mapping")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserMapping {
    @Id
    private UUID userId;
    @OneToMany(mappedBy = "user")
    @JsonBackReference
    private List<Device> devices;
}
