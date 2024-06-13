package com.alcegory.mescloud.repository.production;

import com.alcegory.mescloud.model.entity.equipment.TemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductionOrderTemplateRepository extends JpaRepository<TemplateEntity, Long> {

    @Query("SELECT t FROM production_order_template t " +
            "JOIN t.countingEquipment ce " +
            "WHERE ce.id = :equipmentId")
    Optional<TemplateEntity> findByCountingEquipmentId(@Param("equipmentId") Long equipmentId);
}
