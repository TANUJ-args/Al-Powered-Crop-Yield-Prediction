package com.yieldplanner.repository;

import com.yieldplanner.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByPlotIdOrderByDateDesc(Long plotId);
}
