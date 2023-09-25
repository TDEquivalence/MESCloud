package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.CountingEquipmentKpiDto;
import com.tde.mescloud.model.dto.KpiFilterDto;
import com.tde.mescloud.model.dto.RequestKpiDto;

public interface KpiService {

    CountingEquipmentKpiDto[] computeEquipmentKpi(KpiFilterDto filter);

    Double computeEquipmentQuality(Long equipmentId, RequestKpiDto requestKpiDto);
}
