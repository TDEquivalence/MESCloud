package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.ComposedProductionOrderDto;
import com.alcegory.mescloud.model.dto.ComposedSummaryDto;
import com.alcegory.mescloud.model.dto.RequestComposedDto;
import com.alcegory.mescloud.model.entity.ComposedProductionOrderEntity;

import java.util.List;
import java.util.Optional;

public interface ComposedProductionOrderService {

    Optional<ComposedProductionOrderDto> create(RequestComposedDto productionOrderIds);

    Optional<ComposedProductionOrderDto> create(List<Long> productionOrderIds);

    ComposedProductionOrderEntity saveAndUpdate(ComposedProductionOrderEntity composedEntity);

    void delete(ComposedProductionOrderEntity composedEntity);

    Optional<ComposedProductionOrderEntity> findById(Long id);

    List<ComposedProductionOrderDto> getAll();

    default List<ComposedSummaryDto> findSummarizedWithHits() {
        return findSummarized(true);
    }

    default List<ComposedSummaryDto> findSummarizedWithoutHits() {
        return findSummarized(false);
    }

    List<ComposedSummaryDto> findSummarized(boolean withHits);

    List<ComposedSummaryDto> findCompleted();

    void setProductionOrderApproval(ComposedProductionOrderEntity composed, boolean isApproved);

    void setHitInsertAtInComposed(ComposedProductionOrderEntity composed);
}
