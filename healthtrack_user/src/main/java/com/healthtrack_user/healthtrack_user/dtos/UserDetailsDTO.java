package com.healthtrack_user.healthtrack_user.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.*;


import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDetailsDTO {
    private UUID userId;
    @NotNull
    private String userEmail;
    @NotNull
    private String userPassword;
    @NotNull
    private String userFirstName;
    @NotNull
    private String userLastName;
    @NotNull
    private String userRole;
}
