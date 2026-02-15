package com.yieldplanner.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RecommendationResponse {
    private Long id;
    private Long plotId;
    private String type;
    private String text;
    private LocalDate date;
    private String ruleSource;
}
