package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.exception.ActiveProductionOrderException;
import com.alcegory.mescloud.exception.EquipmentNotFoundException;
import com.alcegory.mescloud.exception.ImsNotFoundException;
import com.alcegory.mescloud.exception.IncompleteConfigurationException;
import com.alcegory.mescloud.model.dto.CountingEquipmentDto;
import com.alcegory.mescloud.model.dto.RequestConfigurationDto;
import com.alcegory.mescloud.model.dto.RequestImsDto;
import com.alcegory.mescloud.service.CountingEquipmentService;
import com.alcegory.mescloud.utility.HttpUtil;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/counting-equipments")
@AllArgsConstructor
public class CountingEquipmentController {

    private static final String IMS_ERROR_CAUSE = "IMS";
    private static final String EQUIPMENT_ERROR_CAUSE = "EQUIPMENT";
    private static final String CONFIG_ERROR_CAUSE = "CONFIG";

    private CountingEquipmentService service;

    @GetMapping
    public ResponseEntity<List<CountingEquipmentDto>> findAll() {
        List<CountingEquipmentDto> countingEquipments = service.findAllWithLastProductionOrder();
        return new ResponseEntity<>(countingEquipments, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CountingEquipmentDto> findById(@PathVariable long id) {
        Optional<CountingEquipmentDto> countingEquipmentOpt = service.findById(id);
        if (countingEquipmentOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(countingEquipmentOpt.get(), HttpStatus.OK);
    }

    @PutMapping("/{equipmentId}/ims")
    public ResponseEntity<CountingEquipmentDto> updateIms(@PathVariable long equipmentId, @RequestBody RequestImsDto request) {
        try {
            CountingEquipmentDto updatedIms = service.updateIms(equipmentId, request.getImsId());
            return new ResponseEntity<>(updatedIms, HttpStatus.OK);
        } catch (EquipmentNotFoundException e) {
            return HttpUtil.responseWithHeaders(HttpStatus.NOT_FOUND, IMS_ERROR_CAUSE, e);
        } catch (ImsNotFoundException e) {
            return HttpUtil.responseWithHeaders(HttpStatus.NOT_FOUND, EQUIPMENT_ERROR_CAUSE, e);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{equipmentId}/configuration")
    public ResponseEntity<CountingEquipmentDto> updateConfiguration(@PathVariable long equipmentId,
                                                                    @RequestBody RequestConfigurationDto request) {
        try {
            CountingEquipmentDto countingEquipment = service.updateConfiguration(equipmentId, request);
            return new ResponseEntity<>(countingEquipment, HttpStatus.OK);
        } catch (IncompleteConfigurationException e) {
            return HttpUtil.responseWithHeaders(HttpStatus.BAD_REQUEST, CONFIG_ERROR_CAUSE, e);
        } catch (EmptyResultDataAccessException e) {
            return HttpUtil.responseWithHeaders(HttpStatus.NOT_FOUND, EQUIPMENT_ERROR_CAUSE, e);
        } catch (ActiveProductionOrderException e) {
            return HttpUtil.responseWithHeaders(HttpStatus.CONFLICT, EQUIPMENT_ERROR_CAUSE, e);
        }
    }
}