package com.yieldplanner.controller;

import com.yieldplanner.dto.FieldConditionRequest;
import com.yieldplanner.dto.PlotRequest;
import com.yieldplanner.dto.PlotResponse;
import com.yieldplanner.dto.PredictionResponse;
import com.yieldplanner.dto.RecommendationResponse;
import com.yieldplanner.entity.FieldCondition;
import com.yieldplanner.service.FieldConditionService;
import com.yieldplanner.service.PlotService;
import com.yieldplanner.service.PredictionService;
import com.yieldplanner.service.RecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plots")
@RequiredArgsConstructor
public class PlotController {

    private final PlotService plotService;
    private final FieldConditionService fieldConditionService;
    private final PredictionService predictionService;
    private final RecommendationService recommendationService;

    @PostMapping
    public ResponseEntity<PlotResponse> createPlot(@Valid @RequestBody PlotRequest request) {
        return ResponseEntity.ok(plotService.createPlot(request));
    }

    @GetMapping
    public ResponseEntity<List<PlotResponse>> getPlotsByFarm(@RequestParam Long farmId) {
        return ResponseEntity.ok(plotService.getPlotsByFarm(farmId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlotResponse> getPlot(@PathVariable Long id) {
        return ResponseEntity.ok(plotService.getPlot(id));
    }

    // ── Field Conditions ────────────────────────────────────────────────────

    @PostMapping("/{id}/conditions")
    public ResponseEntity<FieldCondition> addCondition(
            @PathVariable Long id,
            @RequestBody FieldConditionRequest request) {
        return ResponseEntity.ok(fieldConditionService.addCondition(id, request));
    }

    @GetMapping("/{id}/conditions")
    public ResponseEntity<List<FieldCondition>> getConditions(@PathVariable Long id) {
        return ResponseEntity.ok(fieldConditionService.getConditions(id));
    }

    // ── Predictions ─────────────────────────────────────────────────────────

    @PostMapping("/{id}/predict-yield")
    public ResponseEntity<PredictionResponse> predictYield(@PathVariable Long id) {
        return ResponseEntity.ok(predictionService.predictYield(id));
    }

    @GetMapping("/{id}/predictions")
    public ResponseEntity<List<PredictionResponse>> getPredictions(@PathVariable Long id) {
        return ResponseEntity.ok(predictionService.getPredictions(id));
    }

    // ── Recommendations ─────────────────────────────────────────────────────

    @PostMapping("/{id}/recommendations")
    public ResponseEntity<List<RecommendationResponse>> generateRecommendations(@PathVariable Long id) {
        return ResponseEntity.ok(recommendationService.generateRecommendations(id));
    }

    @GetMapping("/{id}/recommendations")
    public ResponseEntity<List<RecommendationResponse>> getRecommendations(@PathVariable Long id) {
        return ResponseEntity.ok(recommendationService.getRecommendations(id));
    }
}
