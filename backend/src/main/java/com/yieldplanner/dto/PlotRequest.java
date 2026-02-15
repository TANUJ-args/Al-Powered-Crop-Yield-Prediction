package com.yieldplanner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PlotRequest {
    @NotNull
    private Long farmId;

    @NotBlank
    private String cropType;

    @NotNull
    private Double area;

    private String season;
    private LocalDate sowingDate;
}
