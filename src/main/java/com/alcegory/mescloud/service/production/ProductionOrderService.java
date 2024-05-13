package com.alcegory.mescloud.service.production;

import com.alcegory.mescloud.model.dto.ProductionOrderDto;
import com.alcegory.mescloud.model.entity.ProductionOrderEntity;
import com.alcegory.mescloud.model.filter.Filter;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ProductionOrderService {

    Optional<ProductionOrderDto> findDtoByCode(String code);

    Optional<ProductionOrderEntity> findByCode(String code);

    String generateCode();

    boolean hasActiveProductionOrderByEquipmentId(long countingEquipmentId);

    boolean hasActiveProductionOrderByEquipmentCode(String countingEquipmentCode);

    ProductionOrderEntity saveAndUpdate(ProductionOrderEntity productionOrder);

    List<ProductionOrderEntity> saveAndUpdateAll(List<ProductionOrderEntity> productionOrders);

    void delete(ProductionOrderEntity productionOrder);

    void deleteByCode(String productionOrderCode);

    Optional<ProductionOrderEntity> findById(Long id);

    Optional<ProductionOrderDto> findDtoById(Long id);

    List<Long> findExistingIds(List<Long> ids);

    List<ProductionOrderDto> getCompletedWithoutComposedFiltered();

    void setProductionOrderApproval(Long composedOrderId, boolean isApproved);

    List<ProductionOrderDto> findByEquipmentAndPeriod(Long equipmentId, Date startDate, Date endDate);

    boolean isCompleted(String productionOrderCode);

    List<ProductionOrderEntity> findByEquipmentAndPeriod(Long equipmentId, String productionOrderCode, Timestamp startDate,
                                                         Timestamp endDate);

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

    List<ProductionOrderEntity> findCompleted(boolean withoutComposed, Filter filter, Timestamp startDate, Timestamp endDate);
}
