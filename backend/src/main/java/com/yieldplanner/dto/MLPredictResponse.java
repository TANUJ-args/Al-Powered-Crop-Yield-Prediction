package com.yieldplanner.dto;

import lombok.Data;

/**
 * Response from the Python ML service.
 */
@Data
public class MLPredictResponse {
    private Double predicted_yield;
    private String unit;
    private String model_version;
    private String model_type;
}
