package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.CountingEquipmentKpiDto;
import com.tde.mescloud.model.dto.KpiFilterDto;

public interface KpiService {

    CountingEquipmentKpiDto[] computeEquipmentKpi(KpiFilterDto filter);

    Integer computeEquipmentQualityId(Long equipmentId, KpiFilterDto kpiFilter);
}
