package com.healthtrack_user.healthtrack_user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_db")
public class User {
    @Id
    @GeneratedValue
    private UUID userId;
    @Column(name= "userEmail", nullable = false)
    private String userEmail;
    @Column(name= "userPassword", nullable = false)
    private String userPassword;
    @Column(name= "userFirstName", nullable = false)
    private String userFirstName;
    @Column(name= "userLastName", nullable = false)
    private String userLastName;
    @Column(name= "userRole", nullable = false)
    private String userRole;
}
