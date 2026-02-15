package com.yieldplanner.controller;

import com.yieldplanner.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentWeather(
            @RequestParam double lat,
            @RequestParam double lon) {
        return ResponseEntity.ok(weatherService.getCurrentWeather(lat, lon));
    }
}
