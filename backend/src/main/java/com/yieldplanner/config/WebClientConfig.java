package com.yieldplanner.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${app.ml-service.url}")
    private String mlServiceUrl;

    @Bean
    public WebClient mlServiceWebClient() {
        return WebClient.builder()
                .baseUrl(mlServiceUrl)
                .build();
    }

    @Bean
    public WebClient weatherWebClient(@Value("${app.weather.base-url}") String weatherUrl) {
        return WebClient.builder()
                .baseUrl(weatherUrl)
                .build();
    }
}
