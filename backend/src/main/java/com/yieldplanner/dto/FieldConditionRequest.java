package com.yieldplanner.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class FieldConditionRequest {
    private LocalDate date;
    private Double rainfall;
    private Double tempAvg;
    private Double humidity;
    private Double soilMoisture;
    private Double soilPh;
    private Double nitrogen;
    private Double phosphorus;
    private Double potassium;
}
