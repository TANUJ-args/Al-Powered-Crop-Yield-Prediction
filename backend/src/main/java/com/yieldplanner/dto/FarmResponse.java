package com.yieldplanner.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FarmResponse {
    private Long id;
    private String name;
    private String location;
    private Double latitude;
    private Double longitude;
    private String region;
    private String soilType;
    private LocalDateTime createdAt;
}
