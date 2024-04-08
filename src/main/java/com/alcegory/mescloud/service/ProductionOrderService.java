package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.FilterDto;
import com.alcegory.mescloud.model.dto.PaginatedProductionOrderDto;
import com.alcegory.mescloud.model.dto.ProductionOrderDto;
import com.alcegory.mescloud.model.dto.ProductionOrderSummaryDto;
import com.alcegory.mescloud.model.entity.ProductionOrderEntity;
import com.alcegory.mescloud.model.filter.Filter;
import org.springframework.security.core.Authentication;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ProductionOrderService {

    Optional<ProductionOrderDto> findDtoByCode(String code);

    Optional<ProductionOrderEntity> findByCode(String code);

    String generateCode();

    Optional<ProductionOrderDto> create(ProductionOrderDto productionOrderDto, Authentication authentication);

    boolean hasActiveProductionOrderByEquipmentId(long countingEquipmentId);

    boolean hasActiveProductionOrderByEquipmentCode(String countingEquipmentCode);

    Optional<ProductionOrderDto> complete(long countingEquipmentId);

    ProductionOrderEntity saveAndUpdate(ProductionOrderEntity productionOrder);

    List<ProductionOrderEntity> saveAndUpdateAll(List<ProductionOrderEntity> productionOrders);

    void delete(ProductionOrderEntity productionOrder);

    void deleteByCode(String productionOrderCode);

    Optional<ProductionOrderEntity> findById(Long id);

    Optional<ProductionOrderDto> findDtoById(Long id);

    List<Long> findExistingIds(List<Long> ids);

    List<ProductionOrderSummaryDto> getCompletedWithoutComposedFiltered();

    PaginatedProductionOrderDto getCompletedWithoutComposedFiltered(Filter filter);

    void setProductionOrderApproval(Long composedOrderId, boolean isApproved);

    List<ProductionOrderDto> findByEquipmentAndPeriod(Long equipmentId, Date startDate, Date endDate);

    boolean isCompleted(String productionOrderCode);

    List<ProductionOrderEntity> findByEquipmentAndPeriod(Long equipmentId, String productionOrderCode, Timestamp startDate,
                                                         Timestamp endDate);

    List<ProductionOrderSummaryDto> getProductionOrderByComposedId(Long composedId);

    Optional<ProductionOrderDto> editProductionOrder(ProductionOrderDto requestProductionOrder);

    List<ProductionOrderEntity> findByComposedProductionOrderId(Long composedOrderId);

    Long findComposedProductionOrderIdByCode(String code);
}
