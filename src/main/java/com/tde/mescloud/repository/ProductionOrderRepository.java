package com.tde.mescloud.repository;

import com.tde.mescloud.model.entity.ProductionOrderEntity;
import com.tde.mescloud.model.entity.ProductionOrderSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductionOrderRepository extends JpaRepository<ProductionOrderEntity, Long> {

    Optional<ProductionOrderEntity> findByCode(String equipmentOutputCode);

    ProductionOrderEntity findTopByOrderByIdDesc();

    @Query(value = "SELECT * FROM production_order po WHERE (po.equipment_id = :equipmentId AND po.is_completed = false) ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<ProductionOrderEntity> findActive(long equipmentId);

    List<ProductionOrderEntity> findByIdIn(List<Long> ids);

<<<<<<< HEAD
    List<ProductionOrderSummaryEntity> findCompletedAndUnassociated();
=======
    List<ProductionOrderSummaryEntity> findCompletedWithoutComposed();
>>>>>>> 342b74d (Merge pull request #26 from TDEquivalence/feature/MES-230)
}
