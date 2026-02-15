package com.yieldplanner.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PredictionResponse {
    private Long id;
    private Long plotId;
    private Double predictedYield;
    private String unit;
    private LocalDateTime predictionDate;
    private String modelVersion;
}
