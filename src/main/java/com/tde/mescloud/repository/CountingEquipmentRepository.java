package com.tde.mescloud.repository;

import com.tde.mescloud.model.entity.CountingEquipmentEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CountingEquipmentRepository extends CrudRepository<CountingEquipmentEntity, Long> {

    Optional<CountingEquipmentEntity> findByCode(String code);

    @Query("SELECT ce FROM counting_equipment ce " +
            "LEFT JOIN FETCH ce.productionOrders po " +
            "WHERE ce.id = :id AND (po IS NULL OR po.id = (SELECT MAX(p.id) FROM production_order p WHERE p.equipment = ce)) " +
            "ORDER BY ce.id")
    CountingEquipmentEntity findByIdWithActiveProductionOrder(@Param("id") Long id);

    @Query("SELECT ce FROM counting_equipment ce " +
            "LEFT JOIN FETCH ce.productionOrders po " +
            "WHERE po IS NULL OR po.id = (SELECT MAX(p.id) FROM production_order p WHERE p.equipment = ce)" +
            "ORDER BY ce.id")
    List<CountingEquipmentEntity> findAllWithLastProductionOrder();
}
