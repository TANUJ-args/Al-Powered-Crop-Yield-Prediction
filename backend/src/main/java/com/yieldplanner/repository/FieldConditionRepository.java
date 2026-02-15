package com.yieldplanner.repository;

import com.yieldplanner.entity.FieldCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FieldConditionRepository extends JpaRepository<FieldCondition, Long> {
    List<FieldCondition> findByPlotIdOrderByDateDesc(Long plotId);
    Optional<FieldCondition> findFirstByPlotIdOrderByDateDesc(Long plotId);
}
