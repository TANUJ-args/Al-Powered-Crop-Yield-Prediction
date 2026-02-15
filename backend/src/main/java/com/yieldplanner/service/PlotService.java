package com.yieldplanner.service;

import com.yieldplanner.dto.PlotRequest;
import com.yieldplanner.dto.PlotResponse;
import com.yieldplanner.entity.Farm;
import com.yieldplanner.entity.Plot;
import com.yieldplanner.repository.FarmRepository;
import com.yieldplanner.repository.PlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlotService {

    private final PlotRepository plotRepository;
    private final FarmRepository farmRepository;

    public PlotResponse createPlot(PlotRequest req) {
        Farm farm = farmRepository.findById(req.getFarmId())
                .orElseThrow(() -> new RuntimeException("Farm not found"));

        Plot plot = Plot.builder()
                .farm(farm)
                .cropType(req.getCropType())
                .area(req.getArea())
                .season(req.getSeason())
                .sowingDate(req.getSowingDate())
                .build();

        plot = plotRepository.save(plot);
        return toResponse(plot);
    }

    public List<PlotResponse> getPlotsByFarm(Long farmId) {
        return plotRepository.findByFarmId(farmId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PlotResponse getPlot(Long plotId) {
        Plot plot = plotRepository.findById(plotId)
                .orElseThrow(() -> new RuntimeException("Plot not found"));
        return toResponse(plot);
    }

    private PlotResponse toResponse(Plot plot) {
        PlotResponse resp = new PlotResponse();
        resp.setId(plot.getId());
        resp.setFarmId(plot.getFarm().getId());
        resp.setFarmName(plot.getFarm().getName());
        resp.setCropType(plot.getCropType());
        resp.setArea(plot.getArea());
        resp.setSeason(plot.getSeason());
        resp.setSowingDate(plot.getSowingDate());
        resp.setCreatedAt(plot.getCreatedAt());
        return resp;
    }
}
