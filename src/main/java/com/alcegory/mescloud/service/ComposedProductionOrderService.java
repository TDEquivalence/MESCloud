package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.*;
import com.alcegory.mescloud.model.entity.ComposedProductionOrderEntity;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.model.request.RequestComposedDto;

import java.util.List;
import java.util.Optional;

public interface ComposedProductionOrderService {

    Optional<ComposedProductionOrderDto> create(RequestComposedDto productionOrderIds);

    Optional<ComposedProductionOrderDto> create(List<Long> productionOrderIds);

    ComposedProductionOrderEntity saveAndUpdate(ComposedProductionOrderEntity composedEntity);

    void delete(ComposedProductionOrderEntity composedEntity);

    Optional<ComposedProductionOrderEntity> findById(Long id);

    List<ComposedProductionOrderDto> getAll();

    default PaginatedComposedDto findAllSummarizedWithHits() {
        return findAllSummarized(true);
    }

    default PaginatedComposedDto findAllSummarizedWithoutHits() {
        return findAllSummarized(false);
    }

    default PaginatedComposedDto findSummarizedWithHitsFiltered(Filter filter) {
        return findSummarizedFiltered(true, filter);
    }

    default PaginatedComposedDto findSummarizedWithoutHitsFiltered(Filter filter) {
        return findSummarizedFiltered(false, filter);
    }

    PaginatedComposedDto findSummarizedFiltered(boolean withHits, Filter filter);

    PaginatedComposedDto findAllSummarized(boolean withHits);

    List<ComposedSummaryDto> findAllCompleted();

    PaginatedComposedDto findCompletedFiltered(Filter filter);

    void setProductionOrderApproval(ComposedProductionOrderEntity composed, boolean isApproved);

    void setHitInsertAtInComposed(ComposedProductionOrderEntity composed);

    List<ProductionOrderDto> getProductionOrderSummaryByComposedId(Long composedId);

    void deleteComposed(ComposedProductionOrderEntity composedProductionOrder);
}
