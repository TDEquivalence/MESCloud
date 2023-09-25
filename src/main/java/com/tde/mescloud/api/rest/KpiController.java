package com.tde.mescloud.api.rest;

import com.tde.mescloud.model.dto.CountingEquipmentKpiDto;
import com.tde.mescloud.model.dto.KpiAvailabilityDto;
import com.tde.mescloud.model.dto.KpiFilterDto;
import com.tde.mescloud.model.dto.RequestEquipmentKpiDto;
import com.tde.mescloud.service.KpiService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kpi")
@AllArgsConstructor
public class KpiController {

    private final KpiService kpiService;

    @PostMapping("/equipment-counts")
    public ResponseEntity<CountingEquipmentKpiDto[]> getCountingEquipmentKpi(@RequestBody KpiFilterDto filter) {
        CountingEquipmentKpiDto[] countingEquipmentKpiDto = kpiService.computeEquipmentKpi(filter);
        return new ResponseEntity<>(countingEquipmentKpiDto, HttpStatus.OK);
    }

    @PostMapping("/scheduled-time")
    public ResponseEntity<Long> getEquipmentScheduledTime(@RequestBody RequestEquipmentKpiDto filter) {
        Long scheduledTime = kpiService.getTotalScheduledTime(filter);
        return new ResponseEntity<>(scheduledTime, HttpStatus.OK);
    }

    @PostMapping("/availability")
    public ResponseEntity<KpiAvailabilityDto> getEquipmentAvailability(@RequestBody RequestEquipmentKpiDto filter) {
        KpiAvailabilityDto kpiAvailabilityDto = kpiService.getAvailability(filter);
        return new ResponseEntity<>(kpiAvailabilityDto, HttpStatus.OK);
    }
}
