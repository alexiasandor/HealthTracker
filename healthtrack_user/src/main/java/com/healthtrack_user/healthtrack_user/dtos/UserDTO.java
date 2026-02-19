package com.healthtrack_user.healthtrack_user.dtos;


import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import java.util.UUID;
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO extends RepresentationModel<UserDTO>{

    private UUID userId;
    private String userEmail;
    private String userPassword;
    private String userFirstName;
    private String userLastName;
    private String userRole;
}
