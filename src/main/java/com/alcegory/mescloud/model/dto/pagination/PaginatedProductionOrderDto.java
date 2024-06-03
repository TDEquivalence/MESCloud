package com.alcegory.mescloud.model.dto.pagination;

import com.alcegory.mescloud.model.dto.production.ProductionOrderDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaginatedProductionOrderDto {
    private List<ProductionOrderDto> productionOrders;
    private boolean hasNextPage;
}
