package com.yieldplanner.dto;

import lombok.Data;

/**
 * Request body sent to the Python ML service for yield prediction.
 */
@Data
public class MLPredictRequest {
    private String crop_type;
    private String state;
    private String season;
    private String soil_type;
    private Double area_ha;
    private Double rainfall_mm;
    private Double temp_avg_c;
    private Double humidity_pct;
    private Double soil_ph;
    private Double nitrogen_kg;
    private Double phosphorus_kg;
    private Double potassium_kg;
}
