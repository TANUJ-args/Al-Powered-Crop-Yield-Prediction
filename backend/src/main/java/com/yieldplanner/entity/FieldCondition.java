package com.yieldplanner.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "field_conditions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plot_id", nullable = false)
    private Plot plot;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "rainfall")
    private Double rainfall; // mm

    @Column(name = "temp_avg")
    private Double tempAvg; // °C

    @Column(name = "humidity")
    private Double humidity; // %

    @Column(name = "soil_moisture")
    private Double soilMoisture; // %

    @Column(name = "soil_ph")
    private Double soilPh;

    @Column(name = "nitrogen")
    private Double nitrogen; // kg/ha

    @Column(name = "phosphorus")
    private Double phosphorus; // kg/ha

    @Column(name = "potassium")
    private Double potassium; // kg/ha
}
