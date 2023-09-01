package com.tde.mescloud.repository;

import com.tde.mescloud.model.entity.ComposedProductionOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComposedProductionOrderRepository extends JpaRepository<ComposedProductionOrderEntity, Long> {

    @Query("SELECT MAX(CAST(SUBSTRING(cp.code, 3) AS integer)) FROM composed_production_order cp WHERE cp.code LIKE 'CA%'")
    Optional<String> findLastMaxCode();
}
