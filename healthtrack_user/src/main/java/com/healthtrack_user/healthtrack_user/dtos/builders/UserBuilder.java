package com.healthtrack_user.healthtrack_user.dtos.builders;

import com.healthtrack_user.healthtrack_user.dtos.UserDTO;
import com.healthtrack_user.healthtrack_user.dtos.UserDetailsDTO;
import com.healthtrack_user.healthtrack_user.entity.User;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

@NoArgsConstructor
public class UserBuilder {
    // for extern data transport
    public static UserDTO mapToUserDTO(User user){
        return UserDTO.builder()
                .userId(user.getUserId())
                .userEmail(user.getUserEmail())
                .userPassword(user.getUserPassword())
                .userFirstName(user.getUserFirstName())
                .userLastName(user.getUserLastName())
                .userRole(user.getUserRole())
                .build();
    }
// for intern data transport
    public static UserDetailsDTO mapToUserDetailsDTO(User user){
        return UserDetailsDTO.builder()
                .userId(user.getUserId())
                .userEmail(user.getUserEmail())
                .userPassword(user.getUserPassword())
                .userFirstName(user.getUserFirstName())
                .userLastName(user.getUserLastName())
                .userRole(user.getUserRole())
                .build();
    }

    public static User mapToUserEntity(UserDetailsDTO userDetailsDTO){
        return User.builder()
                .userId(userDetailsDTO.getUserId())
                .userEmail(userDetailsDTO.getUserEmail())
                .userPassword(userDetailsDTO.getUserPassword())
                .userFirstName(userDetailsDTO.getUserFirstName())
                .userLastName(userDetailsDTO.getUserLastName())
                .userRole(userDetailsDTO.getUserRole())
                .build();

    }
}
