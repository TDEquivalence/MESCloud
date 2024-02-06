package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.entity.ProductionOrderView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionOrderViewRepository extends JpaRepository<ProductionOrderView, Long> {
}
