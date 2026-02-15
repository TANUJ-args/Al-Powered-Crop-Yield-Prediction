package com.yieldplanner.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yieldplanner.dto.MLPredictRequest;
import com.yieldplanner.dto.MLPredictResponse;
import com.yieldplanner.dto.PredictionResponse;
import com.yieldplanner.entity.FieldCondition;
import com.yieldplanner.entity.Plot;
import com.yieldplanner.entity.Prediction;
import com.yieldplanner.repository.PlotRepository;
import com.yieldplanner.repository.PredictionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PredictionService {

    private final PredictionRepository predictionRepository;
    private final PlotRepository plotRepository;
    private final FieldConditionService fieldConditionService;
    private final WebClient mlServiceWebClient;
    private final ObjectMapper objectMapper;

    public PredictionResponse predictYield(Long plotId) {
        Plot plot = plotRepository.findById(plotId)
                .orElseThrow(() -> new RuntimeException("Plot not found"));

        FieldCondition condition = fieldConditionService.getLatestCondition(plotId);

        // Build ML request
        MLPredictRequest mlReq = new MLPredictRequest();
        mlReq.setCrop_type(plot.getCropType());
        mlReq.setState(plot.getFarm().getRegion() != null ? plot.getFarm().getRegion() : "Andhra Pradesh");
        mlReq.setSeason(plot.getSeason() != null ? plot.getSeason() : "Kharif");
        mlReq.setSoil_type(plot.getFarm().getSoilType() != null ? plot.getFarm().getSoilType() : "Alluvial");
        mlReq.setArea_ha(plot.getArea());

        if (condition != null) {
            mlReq.setRainfall_mm(condition.getRainfall() != null ? condition.getRainfall() : 850.0);
            mlReq.setTemp_avg_c(condition.getTempAvg() != null ? condition.getTempAvg() : 29.0);
            mlReq.setHumidity_pct(condition.getHumidity() != null ? condition.getHumidity() : 70.0);
            mlReq.setSoil_ph(condition.getSoilPh() != null ? condition.getSoilPh() : 6.5);
            mlReq.setNitrogen_kg(condition.getNitrogen() != null ? condition.getNitrogen() : 80.0);
            mlReq.setPhosphorus_kg(condition.getPhosphorus() != null ? condition.getPhosphorus() : 40.0);
            mlReq.setPotassium_kg(condition.getPotassium() != null ? condition.getPotassium() : 50.0);
        } else {
            // Default values if no conditions recorded
            mlReq.setRainfall_mm(850.0);
            mlReq.setTemp_avg_c(29.0);
            mlReq.setHumidity_pct(70.0);
            mlReq.setSoil_ph(6.5);
            mlReq.setNitrogen_kg(80.0);
            mlReq.setPhosphorus_kg(40.0);
            mlReq.setPotassium_kg(50.0);
        }

        // Call ML service
        MLPredictResponse mlResp;
        try {
            mlResp = mlServiceWebClient.post()
                    .uri("/predict")
                    .bodyValue(mlReq)
                    .retrieve()
                    .bodyToMono(MLPredictResponse.class)
                    .block();
        } catch (Exception e) {
            log.warn("ML service unavailable, using fallback prediction: {}", e.getMessage());
            // Fallback: simple rule-based yield estimate
            mlResp = new MLPredictResponse();
            mlResp.setPredicted_yield(estimateFallbackYield(plot.getCropType()));
            mlResp.setUnit("tonnes/hectare");
            mlResp.setModel_version("fallback-v1");
            mlResp.setModel_type("rule-based");
        }

        // Save prediction
        String inputJson;
        try {
            inputJson = objectMapper.writeValueAsString(mlReq);
        } catch (Exception e) {
            inputJson = "{}";
        }

        Prediction prediction = Prediction.builder()
                .plot(plot)
                .predictedYield(mlResp.getPredicted_yield())
                .modelVersion(mlResp.getModel_version())
                .inputSnapshotJson(inputJson)
                .build();

        prediction = predictionRepository.save(prediction);

        return toResponse(prediction);
    }

    public List<PredictionResponse> getPredictions(Long plotId) {
        return predictionRepository.findByPlotIdOrderByPredictionDateDesc(plotId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private double estimateFallbackYield(String cropType) {
        return switch (cropType.toLowerCase()) {
            case "rice" -> 3.5;
            case "wheat" -> 3.0;
            case "maize" -> 2.8;
            case "sugarcane" -> 70.0;
            case "cotton" -> 1.5;
            case "soybean" -> 1.2;
            case "groundnut" -> 1.8;
            default -> 2.5;
        };
    }

    private PredictionResponse toResponse(Prediction p) {
        PredictionResponse resp = new PredictionResponse();
        resp.setId(p.getId());
        resp.setPlotId(p.getPlot().getId());
        resp.setPredictedYield(p.getPredictedYield());
        resp.setUnit("tonnes/hectare");
        resp.setPredictionDate(p.getPredictionDate());
        resp.setModelVersion(p.getModelVersion());
        return resp;
    }
}
