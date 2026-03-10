package com.healthtrack_user.healthtrack_user.controller;

import com.healthtrack_user.healthtrack_user.components.JwtTokenUtil;
import com.healthtrack_user.healthtrack_user.controller.handlers.DuplicateResourceException;
import com.healthtrack_user.healthtrack_user.controller.handlers.InvalidFieldFormatException;
import com.healthtrack_user.healthtrack_user.controller.handlers.ResourceNotFoundException;
import com.healthtrack_user.healthtrack_user.dtos.UserDTO;
import com.healthtrack_user.healthtrack_user.dtos.UserDetailsDTO;
import com.healthtrack_user.healthtrack_user.services.UserService;
import jakarta.validation.Valid;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@CrossOrigin
@RequestMapping("/users")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final RestTemplate restTemplate;
    private final JwtTokenUtil jwtTokenUtil;
    @Value("${URL_DEVICE_API:localhost}")
    private String urlDeviceApi;

    @Autowired
    public UserController(UserService userService, RestTemplate restTemplate, JwtTokenUtil jwtTokenUtil) {
        this.userService = userService;
        this.restTemplate = restTemplate;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @GetMapping()
    public ResponseEntity<List<UserDTO>> getAllUsers(@RequestHeader HttpHeaders headers) {
        if(!jwtTokenUtil.isTokenValid(headers.getFirst("Authorization"))) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        }

        List<UserDTO> userDTOList = userService.findAllUsers();

        for (UserDTO userDto : userDTOList) {
            Link userLink = linkTo(methodOn(UserController.class).getUserById(userDto.getUserId(), new HttpHeaders())).withRel("userDetails");
            userDto.add(userLink);
        }

        return new ResponseEntity<>(userDTOList, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDetailsDTO> getUserById(@PathVariable("id") UUID userId, @RequestHeader HttpHeaders headers) {
        if(!jwtTokenUtil.isTokenValid(headers.getFirst("Authorization"))) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        try {
            UserDetailsDTO userDetailsDTO = userService.findUserById(userId);

            return new ResponseEntity<>(userDetailsDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, e.getStatus());
        }
    }

    @GetMapping(value = "/email/{email}")
    public ResponseEntity<UserDetailsDTO> getUserByEmail(@PathVariable("email") String userEmail, @RequestHeader HttpHeaders headers) {
        if(!jwtTokenUtil.isTokenValid(headers.getFirst("Authorization"))) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        try {
            UserDetailsDTO userDetailsDTO = userService.findUserByEmail(userEmail);

            return new ResponseEntity<>(userDetailsDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, e.getStatus());
        }
    }

    @GetMapping(value = "/{email}/{password}")
    public ResponseEntity<UserDetailsDTO> getUserByEmailAndPassword(@PathVariable("email") String userEmail, @PathVariable("password") String userPassword) {
        try {
            UserDetailsDTO userDetailsDTO = userService.findUserByEmailAndPassword(userEmail, userPassword);

            return new ResponseEntity<>(userDetailsDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, e.getStatus());
        }
    }

    @PostMapping(value = "/token_generation/{userId}")
    public ResponseEntity<String> generateToken(@PathVariable("userId") UUID userId) {
        try {
            UserDetailsDTO userDetailsDTO = userService.findUserById(userId);
            String token = jwtTokenUtil.generateToken(userId, userDetailsDTO.getUserRole());

            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (ResourceNotFoundException | InvalidKeyException | IllegalArgumentException e) {
            return new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping()
    public ResponseEntity<UUID> saveUser(@Valid @RequestBody UserDetailsDTO userDetailsDTO, @RequestHeader HttpHeaders headers) {
        if(!jwtTokenUtil.isTokenValid(headers.getFirst("Authorization"))) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        UUID userId;

        try {
            userId = userService.saveUser(userDetailsDTO);
        } catch (InvalidFieldFormatException | DuplicateResourceException e) {
            return new ResponseEntity<>(null, e.getStatus());
        }

        try {
            JSONObject userObject = new JSONObject();
            userObject.put("userId", userId);
            userObject.put("devices", new JSONArray());

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.setBearerAuth(Objects.requireNonNull(headers.getFirst("Authorization")).substring(7));

            HttpEntity<String> request = new HttpEntity<>(userObject.toString(), httpHeaders);
            String url = urlDeviceApi + "/user_mapping";

            restTemplate.postForEntity(url, request, String.class);

            return new ResponseEntity<>(userId, HttpStatus.CREATED);
        } catch (RestClientException e) {
            LOGGER.error(e.getMessage());
            userService.deleteUserById(userId);

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<UserDetailsDTO> updateUserById(@Valid @RequestBody UserDetailsDTO userDetailsDTO, @PathVariable("id") UUID userId, @RequestHeader HttpHeaders headers) {
        if(!jwtTokenUtil.isTokenValid(headers.getFirst("Authorization"))) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        try {
            UserDetailsDTO updatedUserDetailsDTO = userService.updateUserById(userDetailsDTO, userId);

            return new ResponseEntity<>(updatedUserDetailsDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException | InvalidFieldFormatException | DuplicateResourceException e) {
            return new ResponseEntity<>(null, e.getStatus());
        }

    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable("id") UUID userId, @RequestHeader HttpHeaders headers) {
        if(!jwtTokenUtil.isTokenValid(headers.getFirst("Authorization"))) {
            return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);
        }

        String errorMessage1 = "Error while deleting user with id \"" + userId + "\"!";

        try {
            String url = urlDeviceApi + "/user_mapping/" + userId;

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBearerAuth(Objects.requireNonNull(headers.getFirst("Authorization")).substring(7));
            HttpEntity<String> request = new HttpEntity<>(null, httpHeaders);
            restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);
            //restTemplate.delete(url);
        } catch (RestClientException e) {
            return new ResponseEntity<>(errorMessage1, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            String deleteResponse = userService.deleteUserById(userId);

            return new ResponseEntity<>(deleteResponse, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        }
    }
}
