package com.alcegory.mescloud.service.production;

import com.alcegory.mescloud.model.dto.production.ProductionOrderDto;
import com.alcegory.mescloud.model.entity.production.ProductionOrderEntity;
import com.alcegory.mescloud.model.filter.Filter;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface ProductionOrderService {

    Optional<ProductionOrderDto> findDtoByCode(String code);

    Optional<ProductionOrderEntity> findByCode(String code);

    String generateCode(String sectionPrefix, boolean isSystemGenerated);

    boolean hasActiveProductionOrderByEquipmentId(long countingEquipmentId);

    boolean hasActiveProductionOrderByEquipmentCode(String countingEquipmentCode);

    ProductionOrderEntity saveAndUpdate(ProductionOrderEntity productionOrder);

    List<ProductionOrderEntity> saveAndUpdateAll(List<ProductionOrderEntity> productionOrders);

    void delete(ProductionOrderEntity productionOrder);

    void deleteByCode(String productionOrderCode);

    Optional<ProductionOrderEntity> findById(Long id);

    Optional<ProductionOrderDto> findDtoById(Long id);

    List<Long> findExistingIds(List<Long> ids);

    List<ProductionOrderDto> getCompletedWithoutComposedFiltered(long sectionId);

    void setProductionOrderApproval(Long composedOrderId, boolean isApproved);

    boolean isCompleted(String productionOrderCode);

    List<ProductionOrderDto> getProductionOrderByComposedId(Long composedId);

    List<ProductionOrderEntity> findByComposedProductionOrderId(Long composedOrderId);

    Long findComposedProductionOrderIdByCode(String code);

    Optional<ProductionOrderDto> getProductionOrderById(Long id);

    void completeProductionOrder(ProductionOrderEntity productionOrder);

    ProductionOrderEntity getProductionOrderByCode(String code);

    Optional<ProductionOrderEntity> findActiveByEquipmentId(long equipmentId);

    boolean hasEquipmentActiveProductionOrder(@Param("equipmentId") Long equipmentId);

    ProductionOrderEntity save(ProductionOrderEntity productionOrder);

    Optional<ProductionOrderEntity> findLastByEquipmentId(long equipmentId);

    List<ProductionOrderEntity> findCompleted(long sectionId, boolean withoutComposed, Filter filter,
                                              Timestamp startDate, Timestamp endDate);

    Optional<ProductionOrderEntity> findActiveByEquipmentCode(String equipmentCode);
}
