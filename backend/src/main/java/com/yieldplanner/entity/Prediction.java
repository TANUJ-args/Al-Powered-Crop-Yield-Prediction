package com.yieldplanner.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "predictions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plot_id", nullable = false)
    private Plot plot;

    @Column(name = "predicted_yield", nullable = false)
    private Double predictedYield; // tonnes/hectare

    @Column(name = "prediction_date")
    private LocalDateTime predictionDate;

    @Column(name = "model_version", length = 50)
    private String modelVersion;

    @Column(name = "input_snapshot_json", columnDefinition = "TEXT")
    private String inputSnapshotJson;

    @PrePersist
    protected void onCreate() {
        predictionDate = LocalDateTime.now();
    }
}
