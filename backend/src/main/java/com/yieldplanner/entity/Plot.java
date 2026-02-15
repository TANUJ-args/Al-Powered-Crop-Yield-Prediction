package com.yieldplanner.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "plots")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    @Column(name = "crop_type", nullable = false, length = 50)
    private String cropType;

    @Column(nullable = false)
    private Double area; // hectares

    @Column(length = 20)
    private String season; // Kharif, Rabi, Zaid

    @Column(name = "sowing_date")
    private LocalDate sowingDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
