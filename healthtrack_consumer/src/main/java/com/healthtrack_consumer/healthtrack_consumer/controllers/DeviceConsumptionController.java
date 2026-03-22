package com.healthtrack_consumer.healthtrack_consumer.controllers;

import com.healthtrack_consumer.healthtrack_consumer.components.JwtTokenUtil;
import com.healthtrack_consumer.healthtrack_consumer.services.DeviceConsumptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping(value = "/consumption")
public class DeviceConsumptionController {
    private final DeviceConsumptionService deviceConsumptionService;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public DeviceConsumptionController(DeviceConsumptionService deviceConsumptionService, JwtTokenUtil jwtTokenUtil) {
        this.deviceConsumptionService = deviceConsumptionService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @GetMapping(value = "/{deviceId}/{date}")
    public ResponseEntity<List<Double>> getDeviceConsumptionByDeviceIdAndDate(@PathVariable("deviceId") UUID deviceId, @PathVariable("date") String date, @RequestHeader HttpHeaders headers) {
        if(!jwtTokenUtil.isTokenValid(headers.getFirst("Authorization"))) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        }

        List<Double> hourlyConsumption = deviceConsumptionService.findDeviceConsumptionsByDeviceIdAndDate(deviceId, Date.valueOf(date));

        return new ResponseEntity<>(hourlyConsumption, HttpStatus.OK);
    }
}
