package com.yieldplanner.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {

    private final WebClient weatherWebClient;

    @Value("${app.weather.api-key}")
    private String apiKey;

    /**
     * Get current weather data from OpenWeather API.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getCurrentWeather(double lat, double lon) {
        try {
            return weatherWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/weather")
                            .queryParam("lat", lat)
                            .queryParam("lon", lon)
                            .queryParam("appid", apiKey)
                            .queryParam("units", "metric")
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            log.warn("Weather API call failed: {}", e.getMessage());
            // Return mock weather data
            return Map.of(
                    "main", Map.of(
                            "temp", 29.5,
                            "humidity", 72.0,
                            "pressure", 1013.0
                    ),
                    "weather", java.util.List.of(Map.of(
                            "main", "Clouds",
                            "description", "scattered clouds"
                    )),
                    "wind", Map.of("speed", 3.5),
                    "name", "Demo Location",
                    "source", "mock-data"
            );
        }
    }
}
