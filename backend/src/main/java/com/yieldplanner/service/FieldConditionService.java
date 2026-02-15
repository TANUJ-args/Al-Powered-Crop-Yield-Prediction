package com.yieldplanner.service;

import com.yieldplanner.dto.FieldConditionRequest;
import com.yieldplanner.entity.FieldCondition;
import com.yieldplanner.entity.Plot;
import com.yieldplanner.repository.FieldConditionRepository;
import com.yieldplanner.repository.PlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FieldConditionService {

    private final FieldConditionRepository conditionRepository;
    private final PlotRepository plotRepository;

    public FieldCondition addCondition(Long plotId, FieldConditionRequest req) {
        Plot plot = plotRepository.findById(plotId)
                .orElseThrow(() -> new RuntimeException("Plot not found"));

        FieldCondition condition = FieldCondition.builder()
                .plot(plot)
                .date(req.getDate() != null ? req.getDate() : LocalDate.now())
                .rainfall(req.getRainfall())
                .tempAvg(req.getTempAvg())
                .humidity(req.getHumidity())
                .soilMoisture(req.getSoilMoisture())
                .soilPh(req.getSoilPh())
                .nitrogen(req.getNitrogen())
                .phosphorus(req.getPhosphorus())
                .potassium(req.getPotassium())
                .build();

        return conditionRepository.save(condition);
    }

    public List<FieldCondition> getConditions(Long plotId) {
        return conditionRepository.findByPlotIdOrderByDateDesc(plotId);
    }

    public FieldCondition getLatestCondition(Long plotId) {
        return conditionRepository.findFirstByPlotIdOrderByDateDesc(plotId)
                .orElse(null);
    }
}
