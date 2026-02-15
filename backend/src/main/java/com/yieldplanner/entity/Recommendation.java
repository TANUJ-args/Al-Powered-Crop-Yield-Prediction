package com.yieldplanner.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "recommendations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plot_id", nullable = false)
    private Plot plot;

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private RecommendationType type;

    @Column(columnDefinition = "TEXT")
    private String text;

    private LocalDate date;

    @Column(name = "rule_source", length = 50)
    private String ruleSource; // RULE_BASED or ML_MODEL

    public enum RecommendationType {
        IRRIGATION, FERTILIZER, PEST
    }
}
