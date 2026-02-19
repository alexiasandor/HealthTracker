package com.healthtrack_user.healthtrack_user.services;

import com.healthtrack_user.healthtrack_user.controller.handlers.DuplicateResourceException;
import com.healthtrack_user.healthtrack_user.controller.handlers.InvalidFieldFormatException;
import com.healthtrack_user.healthtrack_user.controller.handlers.ResourceNotFoundException;
import com.healthtrack_user.healthtrack_user.dtos.UserDTO;
import com.healthtrack_user.healthtrack_user.dtos.UserDetailsDTO;
import com.healthtrack_user.healthtrack_user.dtos.builders.UserBuilder;
import com.healthtrack_user.healthtrack_user.dtos.validator.EmailValidator;
import com.healthtrack_user.healthtrack_user.entity.User;
import com.healthtrack_user.healthtrack_user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public List<UserDTO> findAllUsers(){
        List<User> userList = userRepository.findAll();
        List<UserDTO> userDTOList = new ArrayList<>();

        userList.forEach(user -> userDTOList.add(UserBuilder.mapToUserDTO(user)));
        LOGGER.debug("List of all users received");

        return userDTOList;
    }

    public UserDetailsDTO findUserById(UUID id) throws ResourceNotFoundException {
        Optional<User> optionalUser = userRepository.findById(id);

        if(optionalUser.isEmpty()){
            LOGGER.error("User with id \" {}\" was not found !", id);
            throw new ResourceNotFoundException(User.class.getSimpleName() + "user with id: " + id);
        }

        LOGGER.debug("User received");
        return UserBuilder.mapToUserDetailsDTO(optionalUser.get());

    }

    public UUID saveUser (UserDetailsDTO userDetailsDTO) throws InvalidFieldFormatException, DuplicateResourceException {
        if (userDetailsDTO.getUserPassword().length() < 8) {
            LOGGER.error("Password of the user is shorter than 8 characters");
            throw new InvalidFieldFormatException(User.class.getSimpleName() + "user with email:" + userDetailsDTO.getUserEmail());
        }

        if (!userDetailsDTO.getUserRole().equalsIgnoreCase("client") && !userDetailsDTO.getUserRole().equalsIgnoreCase("admin")){
            LOGGER.error("Role of the user is not a valid one !");
            throw new InvalidFieldFormatException(User.class.getSimpleName() + "user with email: " + userDetailsDTO.getUserEmail());
        }

        if(!EmailValidator.isValid(userDetailsDTO.getUserEmail())){
            LOGGER.error("Email of the user does not have a valid format");
            throw new InvalidFieldFormatException(User.class.getSimpleName() + "user with email: " + userDetailsDTO.getUserEmail());
        }

        List<User> userList = userRepository.findAll();
        for(User currentUser : userList){
            if(currentUser.getUserEmail().equals(userDetailsDTO.getUserEmail())){
                LOGGER.error("User with email \"{}\" already exist!", userDetailsDTO.getUserEmail());
                throw new DuplicateResourceException(User.class.getSimpleName() + "user with email: " + userDetailsDTO.getUserEmail());

            }
        }

        userDetailsDTO.setUserRole(userDetailsDTO.getUserRole().toLowerCase());
        userDetailsDTO.setUserPassword(userDetailsDTO.getUserPassword());

        User user = UserBuilder.mapToUserEntity(userDetailsDTO);

        user = userRepository.save(user);
        LOGGER.debug("User with id \"{}\" was inserted in database!", user.getUserId());

        return user.getUserId();
    }

    public UUID updateUser (UserDetailsDTO userDetailsDTO){
        
    }
}
