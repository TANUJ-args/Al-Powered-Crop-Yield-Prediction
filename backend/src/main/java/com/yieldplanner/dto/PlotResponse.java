package com.yieldplanner.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PlotResponse {
    private Long id;
    private Long farmId;
    private String farmName;
    private String cropType;
    private Double area;
    private String season;
    private LocalDate sowingDate;
    private LocalDateTime createdAt;
}
