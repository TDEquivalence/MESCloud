package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.exception.IncompleteConfigurationException;
import com.alcegory.mescloud.model.dto.CountingEquipmentKpiDto;
import com.alcegory.mescloud.model.dto.EquipmentKpiAggregatorDto;
import com.alcegory.mescloud.model.dto.FilterDto;
import com.alcegory.mescloud.model.dto.KpiDto;
import com.alcegory.mescloud.service.kpi.KpiService;
import com.alcegory.mescloud.utility.HttpUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/kpi")
@AllArgsConstructor
public class KpiController {

    private static final String EQUIPMENT_ERROR_CAUSE = "EQUIPMENT";
    private static final String KPI_ERROR_CAUSE = "KPI";

    private final KpiService kpiService;

    @PostMapping("/equipment-counts")
    public ResponseEntity<CountingEquipmentKpiDto[]> getCountingEquipmentKpi(@RequestBody FilterDto filter) {
        try {
            if (filter == null) {
                return ResponseEntity.badRequest().build();
            }

            CountingEquipmentKpiDto[] countingEquipmentKpiDto = kpiService.computeEquipmentKpi(filter);
            return ResponseEntity.ok(countingEquipmentKpiDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/equipment-daily-counts")
    public ResponseEntity<CountingEquipmentKpiDto[]> getEquipmentOutputProductionPerDay(@RequestBody FilterDto filter) {
        try {
            if (filter == null) {
                return ResponseEntity.badRequest().build();
            }

            CountingEquipmentKpiDto[] countingEquipmentKpiDto = kpiService.getEquipmentOutputProductionPerDay(filter);
            return ResponseEntity.ok(countingEquipmentKpiDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{equipmentId}/availability")
    public ResponseEntity<KpiDto> getEquipmentAvailability(@PathVariable long equipmentId, @RequestBody FilterDto filter) {
        try {
            if (filter == null) {
                return ResponseEntity.badRequest().build();
            }

            KpiDto kpiAvailabilityDto = kpiService.computeAvailability(equipmentId, filter);
            return ResponseEntity.ok(kpiAvailabilityDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{equipmentId}/aggregator")
    public ResponseEntity<EquipmentKpiAggregatorDto> getEquipmentKpiAggregator(@PathVariable long equipmentId,
                                                                               @RequestBody FilterDto filter) {
        try {
            EquipmentKpiAggregatorDto kpiAggregatorDto = kpiService.computeEquipmentKpiAggregatorById(equipmentId, filter);
            return new ResponseEntity<>(kpiAggregatorDto, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IncompleteConfigurationException e) {
            return HttpUtil.responseWithHeaders(HttpStatus.CONFLICT, EQUIPMENT_ERROR_CAUSE, e);
        } catch (ArithmeticException e) {
            return HttpUtil.responseWithHeaders(HttpStatus.CONFLICT, KPI_ERROR_CAUSE, e);
        }
    }

    @PostMapping("/aggregator")
    public ResponseEntity<EquipmentKpiAggregatorDto> getEquipmentKpiAggregator(@RequestBody FilterDto filter) {
        try {
            EquipmentKpiAggregatorDto kpiAggregatorDto = kpiService.computeEquipmentKpiAggregator(filter);
            return new ResponseEntity<>(kpiAggregatorDto, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IncompleteConfigurationException e) {
            return HttpUtil.responseWithHeaders(HttpStatus.CONFLICT, EQUIPMENT_ERROR_CAUSE, e);
        } catch (ArithmeticException e) {
            return HttpUtil.responseWithHeaders(HttpStatus.CONFLICT, KPI_ERROR_CAUSE, e);
        }
    }

    @PostMapping("/{equipmentId}/daily-aggregator")
    public ResponseEntity<List<EquipmentKpiAggregatorDto>> getEquipmentKpiAggregatorPerDayById(@PathVariable long equipmentId,
                                                                                               @RequestBody FilterDto filter) {
        try {
            List<EquipmentKpiAggregatorDto> kpiAggregatorsDto = kpiService.computeEquipmentKpiAggregatorPerDayById(equipmentId, filter);
            return new ResponseEntity<>(kpiAggregatorsDto, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/daily-aggregator")
    public ResponseEntity<List<EquipmentKpiAggregatorDto>> getEquipmentKpiAggregatorPerDay(@RequestBody FilterDto filter) {
        try {
            List<EquipmentKpiAggregatorDto> kpiAggregatorsDto = kpiService.computeEquipmentKpiAggregatorPerDay(filter);
            return new ResponseEntity<>(kpiAggregatorsDto, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
