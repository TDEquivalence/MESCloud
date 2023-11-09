package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.ProductionOrderDto;
import com.alcegory.mescloud.model.dto.ProductionOrderSummaryDto;
import com.alcegory.mescloud.model.entity.ProductionOrderEntity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ProductionOrderService {

    Optional<ProductionOrderDto> findByCode(String code);

    String generateCode();

    Optional<ProductionOrderDto> create(ProductionOrderDto productionOrderDto);

    boolean hasActiveProductionOrder(long countingEquipmentId);

    Optional<ProductionOrderDto> complete(long countingEquipmentId);

    ProductionOrderEntity saveAndUpdate(ProductionOrderEntity productionOrder);

    List<ProductionOrderEntity> saveAndUpdateAll(List<ProductionOrderEntity> productionOrders);

    void delete(ProductionOrderEntity productionOrder);

    Optional<ProductionOrderEntity> findById(Long id);

    Optional<ProductionOrderDto> findDtoById(Long id);

    List<Long> findExistingIds(List<Long> ids);

    List<ProductionOrderSummaryDto> getCompletedWithoutComposed();

    void setProductionOrderApproval(Long composedOrderId, boolean isApproved);

    Long calculateScheduledTimeInSeconds(Long equipmentId, Date startDate, Date endDate);

    List<ProductionOrderDto> findByEquipmentAndPeriod(Long equipmentId, Date startDate, Date endDate);

    boolean isCompleted(String productionOrderCode);

    void updateActiveTime(String productionOrderCode, long activeTime);
}
