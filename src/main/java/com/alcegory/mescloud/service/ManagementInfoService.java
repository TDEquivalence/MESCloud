package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.CountingEquipmentInfoDto;
import com.alcegory.mescloud.model.dto.PaginatedProductionOrderDto;
import com.alcegory.mescloud.model.filter.Filter;

import java.util.Optional;

public interface ManagementInfoService {

    Optional<CountingEquipmentInfoDto> findEquipmentWithProductionOrderById(long id);

    PaginatedProductionOrderDto getCompletedWithoutComposedFiltered(Filter filter);
}
