package com.healthtrack_device.healthtrack_device.controllers;

import com.healthtrack_device.healthtrack_device.components.JwtTokenUtil;
import com.healthtrack_device.healthtrack_device.controllers.handlers.ResourceNotFoundException;
import com.healthtrack_device.healthtrack_device.dtos.DeviceDTO;
import com.healthtrack_device.healthtrack_device.dtos.DeviceDetailsDTO;
import com.healthtrack_device.healthtrack_device.dtos.DeviceInfoDTO;
import com.healthtrack_device.healthtrack_device.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@CrossOrigin
@RequestMapping(value = "/device")
public class DeviceController {
    private final DeviceService deviceService;
    private final JwtTokenUtil jwtTokenUtil;
@Autowired
    public DeviceController(DeviceService deviceService, JwtTokenUtil jwtTokenUtil) {
        this.deviceService = deviceService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @GetMapping()
    public ResponseEntity<List<DeviceDTO>> getAllDevices(@RequestHeader HttpHeaders headers) {
        if(!jwtTokenUtil.isTokenValid(headers.getFirst("Authorization"))) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        }

        List<DeviceDTO> deviceDTOList = deviceService.findAllDevices();

        for (DeviceDTO deviceDto : deviceDTOList) {
            Link deviceLink = linkTo(methodOn(DeviceController.class).getDeviceById(deviceDto.getDeviceId(), new HttpHeaders())).withRel("deviceDetails");
            deviceDto.add(deviceLink);
        }

        return new ResponseEntity<>(deviceDTOList, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<DeviceDetailsDTO> getDeviceById(@PathVariable("id") UUID deviceId, @RequestHeader HttpHeaders headers) {
        if(!jwtTokenUtil.isTokenValid(headers.getFirst("Authorization"))) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        try {
            DeviceDetailsDTO deviceDetailsDTO = deviceService.findDeviceById(deviceId);

            return new ResponseEntity<>(deviceDetailsDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, e.getStatus());
        }
    }

    @GetMapping(value = "/user/{id}")
    public ResponseEntity<List<DeviceDTO>> getDeviceByUserId(@PathVariable("id") UUID userId, @RequestHeader HttpHeaders headers) {
        if(!jwtTokenUtil.isTokenValid(headers.getFirst("Authorization"))) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        }

        List<DeviceDTO> deviceDTOList = deviceService.findDevicesByUserId(userId);

        for (DeviceDTO deviceDto : deviceDTOList) {
            Link deviceLink = linkTo(methodOn(DeviceController.class).getDeviceById(deviceDto.getDeviceId(), new HttpHeaders())).withRel("deviceDetails");
            deviceDto.add(deviceLink);
        }

        return new ResponseEntity<>(deviceDTOList, HttpStatus.OK);
    }


    @PostMapping()
    public ResponseEntity<UUID> saveDevice(@Valid @RequestBody DeviceDetailsDTO deviceDetailsDTO, @RequestHeader HttpHeaders headers) {
        if(!jwtTokenUtil.isTokenValid(headers.getFirst("Authorization"))) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        UUID deviceId = deviceService.saveDevice(deviceDetailsDTO);

        //DeviceInfoDTO deviceInfoDTO = new DeviceInfoDTO(deviceId, deviceDetailsDTO.getMaximumHourlyEnergyConsumption());
        //rabbitSender.sendCreateUpdateDevice(deviceInfoDTO);

        return new ResponseEntity<>(deviceId, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<DeviceDetailsDTO> updateDeviceById(@Valid @RequestBody DeviceDetailsDTO deviceDetailsDTO, @PathVariable("id") UUID deviceId, @RequestHeader HttpHeaders headers) {
        if(!jwtTokenUtil.isTokenValid(headers.getFirst("Authorization"))) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        try {
            DeviceDetailsDTO updatedDeviceDetailsDTO = deviceService.updateDeviceById(deviceDetailsDTO, deviceId);

            //DeviceInfoDTO deviceInfoDTO = new DeviceInfoDTO(updatedDeviceDetailsDTO.getDeviceId(), updatedDeviceDetailsDTO.getMaximumHourlyEnergyConsumption());
            //rabbitSender.sendCreateUpdateDevice(deviceInfoDTO);

            return new ResponseEntity<>(updatedDeviceDetailsDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, e.getStatus());
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteDeviceById(@PathVariable("id") UUID deviceId, @RequestHeader HttpHeaders headers) {
        if(!jwtTokenUtil.isTokenValid(headers.getFirst("Authorization"))) {
            return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);
        }

        try {
            String deleteResponse = deviceService.deleteDeviceById(deviceId);
            //rabbitSender.sendDeleteDevice(deviceId);

            return new ResponseEntity<>(deleteResponse, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        }
    }

}
