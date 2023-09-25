package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.*;

public interface KpiService {

    CountingEquipmentKpiDto[] computeEquipmentKpi(KpiFilterDto filter);

    Double computeEquipmentQuality(Long equipmentId, RequestKpiDto requestKpiDto);

    Long getTotalScheduledTime(RequestEquipmentKpiDto filter);

    KpiAvailabilityDto getAvailability(RequestEquipmentKpiDto filter);
}
