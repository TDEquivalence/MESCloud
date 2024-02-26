package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.entity.ComposedProductionOrderEntity;
import com.alcegory.mescloud.model.entity.ComposedSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComposedProductionOrderRepository extends JpaRepository<ComposedProductionOrderEntity, Long> {

    @Query("SELECT MAX(CAST(SUBSTRING(cp.code, 6) AS integer)) FROM composed_production_order cp WHERE cp.code LIKE 'OBOCP%'")
    Optional<String> findLastMaxCode();

    Optional<ComposedProductionOrderEntity> findTopByOrderByIdDesc();

    List<ComposedSummaryEntity> getOpenComposedSummaries(boolean withHits, Timestamp startDate, Timestamp endDate);

    List<ComposedSummaryEntity> findCompleted(Timestamp startDate, Timestamp endDate);

    List<ComposedSummaryEntity> findAllComposed(Timestamp startDate, Timestamp endDate);
}
