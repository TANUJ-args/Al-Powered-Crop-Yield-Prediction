package com.yieldplanner.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FarmRequest {
    @NotBlank
    private String name;
    private String location;
    private Double latitude;
    private Double longitude;
    private String region;
    private String soilType;
}
