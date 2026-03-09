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
import jakarta.transaction.Transactional;
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

    @Transactional
    public List<UserDTO> findAllUsers(){
        List<User> userList = userRepository.findAll();
        List<UserDTO> userDTOList = new ArrayList<>();

        userList.forEach(user -> userDTOList.add(UserBuilder.mapToUserDTO(user)));
        LOGGER.debug("List of all users received");

        return userDTOList;
    }

    @Transactional
    public UserDetailsDTO findUserById(UUID id) throws ResourceNotFoundException {
        Optional<User> optionalUser = userRepository.findById(id);

        if(optionalUser.isEmpty()){
            LOGGER.error("User with id \" {}\" was not found !", id);
            throw new ResourceNotFoundException(User.class.getSimpleName() + "user with id: " + id);
        }

        LOGGER.debug("User received");
        return UserBuilder.mapToUserDetailsDTO(optionalUser.get());

    }

    @Transactional
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

    @Transactional
    public UserDetailsDTO updateUserById (UserDetailsDTO userDetailsDTO, UUID userId) throws ResourceNotFoundException, InvalidFieldFormatException, DuplicateResourceException{
        Optional<User> optionalUser =  userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            LOGGER.error("User with id \"{}\" was not found!", userId);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " user with id: " + userId);
        }

        if (!EmailValidator.isValid(optionalUser.get().getUserEmail())) {
            LOGGER.error("Email of the user doesn't have a valid format!");
            throw new InvalidFieldFormatException(User.class.getSimpleName() + " user with email: " + optionalUser.get().getUserEmail());
        }

        List<User> userList = userRepository.findAll();
        for (User user1 : userList) {
            if (!user1.getUserId().equals(userId)) {
                if (user1.getUserEmail().equals(userDetailsDTO.getUserEmail())) {
                    LOGGER.error("User with email \"{}\" already exists!", optionalUser.get().getUserEmail());
                    throw new DuplicateResourceException(User.class.getSimpleName() + " user with email:" + optionalUser.get().getUserEmail());
                }
            }
        }

        User updatedUser = optionalUser.map(user -> {
            user.setUserId(userDetailsDTO.getUserId());
            user.setUserEmail(userDetailsDTO.getUserEmail());
            user.setUserPassword(userDetailsDTO.getUserPassword());
            user.setUserFirstName(userDetailsDTO.getUserFirstName());
            user.setUserLastName(user.getUserLastName());
            user.setUserRole(userDetailsDTO.getUserRole());

            return userRepository.save(user);
        }).get();

        LOGGER.debug("User with id \"{}\" was updated in db!", updatedUser.getUserId());

        return UserBuilder.mapToUserDetailsDTO(updatedUser);
    }

    @Transactional
    public String deleteUserById(UUID userId) throws ResourceNotFoundException {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            LOGGER.error("User with id \"{}\" was not found", userId);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " user with id: " + userId);
        }

        userRepository.deleteById(userId);
        LOGGER.debug("User with id \"{}\" was deleted from the db!", optionalUser.get().getUserId());

        return "User with id \"" + userId + "\" was deleted from the db!";
    }
    @Transactional
    public UserDetailsDTO findUserByEmail(String userEmail) throws ResourceNotFoundException {
        Optional<User> optionalUser = userRepository.findByUserEmail(userEmail);

        if (optionalUser.isEmpty()) {
            LOGGER.error("User with email \"{}\" was not found!", userEmail);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " user with email: " + userEmail);
        }

        LOGGER.debug("User received!");

        return UserBuilder.mapToUserDetailsDTO(optionalUser.get());
    }

    @Transactional
    public UserDetailsDTO findUserByEmailAndPassword(String userEmail, String userPassword) throws ResourceNotFoundException {
        Optional<User> optionalUserEmail = userRepository.findByUserEmail(userEmail);

        if (optionalUserEmail.isEmpty()) {
            LOGGER.error("User with email \"{}\" was not found!", userEmail);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " user with email: " + userEmail);
        }
        if(!optionalUserEmail.get().getUserPassword().equals(userPassword)) {
            LOGGER.error("User password is not valid!");
            throw new ResourceNotFoundException(User.class.getSimpleName() + " user with password: " + userPassword);
        }

        Optional<User> optionalUser = userRepository.findByUserEmailAndUserPassword(userEmail, optionalUserEmail.get().getUserPassword());

        if (optionalUser.isEmpty()) {
            LOGGER.error("User with email \"{}\" and password \"{}\" was not found!", userEmail, userPassword);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " user with email: " + userEmail);
        }

        LOGGER.debug("User received!");

        return UserBuilder.mapToUserDetailsDTO(optionalUser.get());
    }
}
