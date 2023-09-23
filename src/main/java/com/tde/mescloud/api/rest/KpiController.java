package com.tde.mescloud.api.rest;

import com.tde.mescloud.model.dto.CountingEquipmentKpiDto;
import com.tde.mescloud.model.dto.KpiFilterDto;
import com.tde.mescloud.model.dto.RequestKpiDto;
import com.tde.mescloud.service.KpiService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/{equipmentId}/quality")
    public ResponseEntity<Double> getQuality(
            @PathVariable Long equipmentId, @RequestBody RequestKpiDto requestKpiDto) {

        Double quality = kpiService.computeEquipmentQuality(equipmentId, requestKpiDto);
        return ResponseEntity.ok(quality);
    }
}
