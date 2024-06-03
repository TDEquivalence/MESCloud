package com.alcegory.mescloud.service.management;

import com.alcegory.mescloud.model.dto.equipment.CountingEquipmentDto;
import com.alcegory.mescloud.model.dto.equipment.CountingEquipmentInfoDto;
import com.alcegory.mescloud.model.dto.pagination.PaginatedProductionOrderDto;
import com.alcegory.mescloud.model.filter.Filter;

import java.util.Optional;

public interface ManagementInfoService {

    Optional<CountingEquipmentInfoDto> findEquipmentWithProductionOrderById(long id);

    PaginatedProductionOrderDto getCompletedWithoutComposedFiltered(Filter filter);

    CountingEquipmentDto findEquipmentById(long id);
}
