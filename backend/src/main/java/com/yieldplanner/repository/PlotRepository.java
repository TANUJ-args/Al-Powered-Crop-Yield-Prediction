package com.yieldplanner.repository;

import com.yieldplanner.entity.Plot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlotRepository extends JpaRepository<Plot, Long> {
    List<Plot> findByFarmId(Long farmId);
}
