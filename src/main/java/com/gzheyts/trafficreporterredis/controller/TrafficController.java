package com.gzheyts.trafficreporterredis.controller;

import com.gzheyts.trafficreporterredis.model.Traffic;
import com.gzheyts.trafficreporterredis.rest.ApiResponse;
import com.gzheyts.trafficreporterredis.service.TrafficService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@Slf4j
@RequiredArgsConstructor
public class TrafficController {

    private final TrafficService trafficService;

    @PostMapping("visited_links")
    @ApiOperation("Save traffic")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse saveTraffic(@ApiParam(value = "payload with traffic links", required = true) @RequestBody Traffic traffic) {
        log.info("save traffic request - traffic: {}", traffic);
        trafficService.saveTraffic(traffic, Instant.now().getEpochSecond());
        return ApiResponse.saved();
    }

    @GetMapping("visited_domains")
    @ApiOperation("List unique visited domains within period")
    public ApiResponse listUniqueDomains(
            @ApiParam(value = "unix time") @RequestParam(required = false) Long from,
            @ApiParam(value = "unix time") @RequestParam(required = false) Long to) {

        log.info("list domains request - from: {}, to: {}", from, to);
        return ApiResponse.domains(trafficService.queryUniqueDomains(from, to));
    }
}
