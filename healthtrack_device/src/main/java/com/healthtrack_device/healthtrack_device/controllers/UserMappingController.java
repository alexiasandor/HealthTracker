package com.healthtrack_device.healthtrack_device.controllers;

import com.healthtrack_device.healthtrack_device.components.JwtTokenUtil;
import com.healthtrack_device.healthtrack_device.controllers.handlers.ResourceNotFoundException;
import com.healthtrack_device.healthtrack_device.dtos.UserMappingDetailsDTO;
import com.healthtrack_device.healthtrack_device.service.DeviceService;
import com.healthtrack_device.healthtrack_device.service.UserMappingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
@RestController
@CrossOrigin
@RequestMapping(value = "/user_mapping")
public class UserMappingController {
    private final UserMappingService userMappingService;
    private final DeviceService deviceService;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public UserMappingController(UserMappingService userMappingService, DeviceService deviceService, JwtTokenUtil jwtTokenUtil) {
        this.userMappingService = userMappingService;
        this.deviceService = deviceService;
        this.jwtTokenUtil = jwtTokenUtil;
    }
    @PostMapping()
    public ResponseEntity<UUID> saveUserMapping(@Valid @RequestBody UserMappingDetailsDTO userMappingDetailsDTO, @RequestHeader HttpHeaders headers) {
        if(!jwtTokenUtil.isTokenValid(headers.getFirst("Authorization"))) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        UUID userId = userMappingService.saveUserMapping(userMappingDetailsDTO);

        return new ResponseEntity<>(userId, HttpStatus.CREATED);
    }
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteUserMappingById(@PathVariable("id") UUID userId, @RequestHeader HttpHeaders headers) {
        if(!jwtTokenUtil.isTokenValid(headers.getFirst("Authorization"))) {
            return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);
        }

        try {
            deviceService.updateDevicesByUserId(userId);

            String deleteResponse = userMappingService.deleteUserMappingById(userId);

            return new ResponseEntity<>(deleteResponse, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        }

    }

}
