package com.alcegory.mescloud.api.rest.section;

import com.alcegory.mescloud.api.rest.base.SectionBaseController;
import com.alcegory.mescloud.exception.IncompleteConfigurationException;
import com.alcegory.mescloud.model.dto.equipment.CountingEquipmentKpiDto;
import com.alcegory.mescloud.model.dto.equipment.EquipmentKpiAggregatorDto;
import com.alcegory.mescloud.model.filter.FilterDto;
import com.alcegory.mescloud.service.kpi.KpiManagementServiceImpl;
import com.alcegory.mescloud.utility.HttpUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@AllArgsConstructor
public class KpiController extends SectionBaseController {

    private static final String KPI_URL = "/kpi";

    private static final String EQUIPMENT_ERROR_CAUSE = "EQUIPMENT";
    private static final String KPI_ERROR_CAUSE = "KPI";

    private final KpiManagementServiceImpl kpiManagementService;

    @PostMapping(KPI_URL + "/equipment-counts")
    public ResponseEntity<CountingEquipmentKpiDto[]> getCountingEquipmentKpi(@RequestBody FilterDto filter) {
        try {
            if (filter == null) {
                return ResponseEntity.badRequest().build();
            }

            CountingEquipmentKpiDto[] countingEquipmentKpiDto = kpiManagementService.computeEquipmentKpi(filter);
            return ResponseEntity.ok(countingEquipmentKpiDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(KPI_URL + "/equipment-daily-counts")
    public ResponseEntity<CountingEquipmentKpiDto[]> getEquipmentOutputProductionPerDay(@RequestBody FilterDto filter) {
        try {
            if (filter == null) {
                return ResponseEntity.badRequest().build();
            }

            CountingEquipmentKpiDto[] countingEquipmentKpiDto = kpiManagementService.getEquipmentOutputProductionPerDay(filter);
            return ResponseEntity.ok(countingEquipmentKpiDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(KPI_URL + "/{equipmentId}/aggregator")
    public ResponseEntity<EquipmentKpiAggregatorDto> getEquipmentKpiAggregator(@PathVariable long equipmentId,
                                                                               @RequestBody FilterDto filter) {
        try {
            EquipmentKpiAggregatorDto kpiAggregatorDto = kpiManagementService.computeEquipmentKpiAggregatorById(equipmentId, filter);
            return new ResponseEntity<>(kpiAggregatorDto, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IncompleteConfigurationException e) {
            return HttpUtil.responseWithHeaders(HttpStatus.CONFLICT, EQUIPMENT_ERROR_CAUSE, e);
        } catch (ArithmeticException e) {
            return HttpUtil.responseWithHeaders(HttpStatus.CONFLICT, KPI_ERROR_CAUSE, e);
        }
    }

    @PostMapping(KPI_URL + "/aggregator")
    public ResponseEntity<EquipmentKpiAggregatorDto> getEquipmentKpiAggregator(@RequestBody FilterDto filter) {
        try {
            EquipmentKpiAggregatorDto kpiAggregatorDto = kpiManagementService.computeAllEquipmentKpiAggregator(filter);
            return new ResponseEntity<>(kpiAggregatorDto, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IncompleteConfigurationException e) {
            return HttpUtil.responseWithHeaders(HttpStatus.CONFLICT, EQUIPMENT_ERROR_CAUSE, e);
        } catch (ArithmeticException e) {
            return HttpUtil.responseWithHeaders(HttpStatus.CONFLICT, KPI_ERROR_CAUSE, e);
        }
    }

    @PostMapping(KPI_URL + "/{equipmentId}/daily-aggregator")
    public ResponseEntity<List<EquipmentKpiAggregatorDto>> getEquipmentKpiAggregatorPerDayById(@PathVariable long equipmentId,
                                                                                               @RequestBody FilterDto filter) {
        try {
            List<EquipmentKpiAggregatorDto> kpiAggregatorsDto = kpiManagementService.computeEquipmentKpiAggregatorPerDayById(equipmentId, filter);
            return new ResponseEntity<>(kpiAggregatorsDto, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(KPI_URL + "/daily-aggregator")
    public ResponseEntity<List<EquipmentKpiAggregatorDto>> getEquipmentKpiAggregatorPerDay(@RequestBody FilterDto filter) {
        try {
            List<EquipmentKpiAggregatorDto> kpiAggregatorsDto = kpiManagementService.computeEquipmentKpiAggregatorPerDay(filter);
            return new ResponseEntity<>(kpiAggregatorsDto, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
