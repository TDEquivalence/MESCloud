package com.alcegory.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaginatedProductionOrderDto {
    private List<ProductionOrderSummaryDto> productionOrders;
    private boolean hasNextPage;
}
