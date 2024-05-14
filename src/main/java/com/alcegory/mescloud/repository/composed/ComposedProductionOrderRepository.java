package com.alcegory.mescloud.repository.composed;

import com.alcegory.mescloud.model.entity.production.ComposedProductionOrderEntity;
import com.alcegory.mescloud.model.entity.production.ComposedSummaryEntity;
import com.alcegory.mescloud.model.filter.Filter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComposedProductionOrderRepository extends JpaRepository<ComposedProductionOrderEntity, Long> {

    @Query("SELECT MAX(CAST(SUBSTRING(cp.code, 6) AS integer)) FROM composed_production_order cp WHERE cp.code LIKE 'OBOCP%'")
    Optional<String> findLastMaxCode();

    Optional<ComposedProductionOrderEntity> findTopByOrderByIdDesc();

    List<ComposedSummaryEntity> getOpenComposedSummaries(boolean withHits, Filter filter, Long composedId);

    List<ComposedSummaryEntity> findCompleted(Filter filter, Long composedId);

    List<ComposedSummaryEntity> findAllComposed(Timestamp startDate, Timestamp endDate);

    @Query(value = "SELECT * FROM your_view_name WHERE id = :id AND hit_inserted_at IS NULL AND sample_amount IS NOT NULL", nativeQuery = true)
    Optional<ComposedSummaryEntity> findComposedSummaryEntityWithoutHitsById(@Param("id") Integer id);
}
