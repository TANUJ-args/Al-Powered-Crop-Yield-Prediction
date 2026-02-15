package com.yieldplanner.repository;

import com.yieldplanner.entity.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {
    List<Prediction> findByPlotIdOrderByPredictionDateDesc(Long plotId);
}
