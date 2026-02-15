package com.yieldplanner.service;

import com.yieldplanner.dto.RecommendationResponse;
import com.yieldplanner.entity.FieldCondition;
import com.yieldplanner.entity.Plot;
import com.yieldplanner.entity.Recommendation;
import com.yieldplanner.entity.Recommendation.RecommendationType;
import com.yieldplanner.repository.PlotRepository;
import com.yieldplanner.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final PlotRepository plotRepository;
    private final FieldConditionService fieldConditionService;

    /**
     * Generate rule-based recommendations for a plot based on current field conditions.
     */
    public List<RecommendationResponse> generateRecommendations(Long plotId) {
        Plot plot = plotRepository.findById(plotId)
                .orElseThrow(() -> new RuntimeException("Plot not found"));

        FieldCondition condition = fieldConditionService.getLatestCondition(plotId);

        List<Recommendation> newRecs = new ArrayList<>();

        // ── Irrigation Rules ────────────────────────────────────────────────
        if (condition != null) {
            if (condition.getRainfall() != null && condition.getRainfall() < 50) {
                if (condition.getSoilMoisture() != null && condition.getSoilMoisture() < 30) {
                    newRecs.add(Recommendation.builder()
                            .plot(plot)
                            .type(RecommendationType.IRRIGATION)
                            .text("Soil moisture is low (" + condition.getSoilMoisture() +
                                  "%) and recent rainfall is only " + condition.getRainfall() +
                                  " mm. Recommend immediate irrigation — apply 30-40 mm of water.")
                            .date(LocalDate.now())
                            .ruleSource("RULE_BASED")
                            .build());
                }
            }

            if (condition.getTempAvg() != null && condition.getTempAvg() > 35) {
                newRecs.add(Recommendation.builder()
                        .plot(plot)
                        .type(RecommendationType.IRRIGATION)
                        .text("High temperature detected (" + condition.getTempAvg() +
                              "°C). Consider increasing irrigation frequency to prevent heat stress on " +
                              plot.getCropType() + " crop.")
                        .date(LocalDate.now())
                        .ruleSource("RULE_BASED")
                        .build());
            }

            // ── Fertilizer Rules ────────────────────────────────────────────
            Map<String, double[]> cropNPKOptimal = Map.of(
                    "rice", new double[]{80, 40, 40},
                    "wheat", new double[]{100, 50, 40},
                    "maize", new double[]{90, 45, 35},
                    "sugarcane", new double[]{120, 60, 60},
                    "cotton", new double[]{70, 35, 35},
                    "soybean", new double[]{20, 60, 40},
                    "groundnut", new double[]{20, 40, 50}
            );

            String cropKey = plot.getCropType().toLowerCase();
            double[] optimal = cropNPKOptimal.getOrDefault(cropKey, new double[]{80, 40, 40});

            if (condition.getNitrogen() != null && condition.getNitrogen() < optimal[0] * 0.7) {
                newRecs.add(Recommendation.builder()
                        .plot(plot)
                        .type(RecommendationType.FERTILIZER)
                        .text("Nitrogen level (" + condition.getNitrogen() + " kg/ha) is below optimal for " +
                              plot.getCropType() + " (recommended: " + optimal[0] +
                              " kg/ha). Apply urea or ammonium sulfate to supplement.")
                        .date(LocalDate.now())
                        .ruleSource("RULE_BASED")
                        .build());
            }

            if (condition.getPhosphorus() != null && condition.getPhosphorus() < optimal[1] * 0.7) {
                newRecs.add(Recommendation.builder()
                        .plot(plot)
                        .type(RecommendationType.FERTILIZER)
                        .text("Phosphorus level (" + condition.getPhosphorus() + " kg/ha) is low for " +
                              plot.getCropType() + " (recommended: " + optimal[1] +
                              " kg/ha). Apply DAP (Di-Ammonium Phosphate).")
                        .date(LocalDate.now())
                        .ruleSource("RULE_BASED")
                        .build());
            }

            if (condition.getPotassium() != null && condition.getPotassium() < optimal[2] * 0.7) {
                newRecs.add(Recommendation.builder()
                        .plot(plot)
                        .type(RecommendationType.FERTILIZER)
                        .text("Potassium level (" + condition.getPotassium() + " kg/ha) is low for " +
                              plot.getCropType() + " (recommended: " + optimal[2] +
                              " kg/ha). Apply MOP (Muriate of Potash).")
                        .date(LocalDate.now())
                        .ruleSource("RULE_BASED")
                        .build());
            }

            if (condition.getSoilPh() != null) {
                if (condition.getSoilPh() < 5.5) {
                    newRecs.add(Recommendation.builder()
                            .plot(plot)
                            .type(RecommendationType.FERTILIZER)
                            .text("Soil pH is acidic (" + condition.getSoilPh() +
                                  "). Apply lime (calcium carbonate) to raise pH to 6.0-7.0 range.")
                            .date(LocalDate.now())
                            .ruleSource("RULE_BASED")
                            .build());
                } else if (condition.getSoilPh() > 8.0) {
                    newRecs.add(Recommendation.builder()
                            .plot(plot)
                            .type(RecommendationType.FERTILIZER)
                            .text("Soil pH is alkaline (" + condition.getSoilPh() +
                                  "). Apply gypsum or sulfur to lower pH.")
                            .date(LocalDate.now())
                            .ruleSource("RULE_BASED")
                            .build());
                }
            }
        }

        // ── Pest Rules (Season + Crop based) ────────────────────────────────
        Map<String, Map<String, String>> pestRules = Map.of(
                "rice", Map.of(
                        "kharif", "Watch for Brown Plant Hopper (BPH) and Blast disease during monsoon. Apply Neem-based pesticide as preventive.",
                        "rabi", "Monitor for Stem Borer. Use pheromone traps and apply Chlorantraniliprole if infestation detected."
                ),
                "wheat", Map.of(
                        "rabi", "Watch for Aphids and Rust disease. Apply Propiconazole fungicide if yellow rust spots appear."
                ),
                "cotton", Map.of(
                        "kharif", "Monitor for American Bollworm. Install pheromone traps and use Bt cotton varieties if possible."
                ),
                "maize", Map.of(
                        "kharif", "Watch for Fall Armyworm. Scout fields regularly and apply Emamectin Benzoate if damage exceeds threshold."
                )
        );

        String cropLower = plot.getCropType().toLowerCase();
        String seasonLower = plot.getSeason() != null ? plot.getSeason().toLowerCase() : "kharif";

        if (pestRules.containsKey(cropLower)) {
            Map<String, String> seasonPests = pestRules.get(cropLower);
            if (seasonPests.containsKey(seasonLower)) {
                newRecs.add(Recommendation.builder()
                        .plot(plot)
                        .type(RecommendationType.PEST)
                        .text(seasonPests.get(seasonLower))
                        .date(LocalDate.now())
                        .ruleSource("RULE_BASED")
                        .build());
            }
        }

        // If humidity is high, general pest warning
        if (condition != null && condition.getHumidity() != null && condition.getHumidity() > 85) {
            newRecs.add(Recommendation.builder()
                    .plot(plot)
                    .type(RecommendationType.PEST)
                    .text("High humidity (" + condition.getHumidity() +
                          "%) increases risk of fungal diseases. Apply preventive fungicide and ensure proper plant spacing for air circulation.")
                    .date(LocalDate.now())
                    .ruleSource("RULE_BASED")
                    .build());
        }

        // Save all recommendations
        recommendationRepository.saveAll(newRecs);

        // Return all recommendations for this plot (including previously generated)
        return recommendationRepository.findByPlotIdOrderByDateDesc(plotId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<RecommendationResponse> getRecommendations(Long plotId) {
        return recommendationRepository.findByPlotIdOrderByDateDesc(plotId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private RecommendationResponse toResponse(Recommendation r) {
        RecommendationResponse resp = new RecommendationResponse();
        resp.setId(r.getId());
        resp.setPlotId(r.getPlot().getId());
        resp.setType(r.getType().name());
        resp.setText(r.getText());
        resp.setDate(r.getDate());
        resp.setRuleSource(r.getRuleSource());
        return resp;
    }
}
