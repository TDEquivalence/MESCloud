package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.exception.*;
import com.alcegory.mescloud.model.dto.CountingEquipmentDto;
import com.alcegory.mescloud.model.request.RequestById;
import com.alcegory.mescloud.model.request.RequestConfigurationDto;
import com.alcegory.mescloud.service.CountingEquipmentService;
import com.alcegory.mescloud.utility.HttpUtil;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    private static final String PLC_ERROR_CAUSE = "PLC";

    private CountingEquipmentService service;

    @GetMapping
    public ResponseEntity<List<CountingEquipmentDto>> findAll() {
        List<CountingEquipmentDto> countingEquipments = service.findAllWithLastProductionOrder();
        return new ResponseEntity<>(countingEquipments, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CountingEquipmentDto> findById(@PathVariable long id) {
        Optional<CountingEquipmentDto> countingEquipmentOpt = service.findEquipmentWithProductionOrderById(id);
        if (countingEquipmentOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(countingEquipmentOpt.get(), HttpStatus.OK);
    }

    @PutMapping("/{equipmentId}/ims")
    public ResponseEntity<CountingEquipmentDto> updateIms(@PathVariable long equipmentId, @RequestBody RequestById request,
                                                          Authentication authentication) {
        try {
            CountingEquipmentDto updatedIms = service.updateIms(equipmentId, request.getId(), authentication);
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
                                                                    @RequestBody RequestConfigurationDto request,
                                                                    Authentication authentication) {
        try {
            CountingEquipmentDto countingEquipment = service.updateConfiguration(equipmentId, request, authentication);
            return new ResponseEntity<>(countingEquipment, HttpStatus.OK);
        } catch (IncompleteConfigurationException e) {
            return HttpUtil.responseWithHeaders(HttpStatus.BAD_REQUEST, CONFIG_ERROR_CAUSE, e);
        } catch (EmptyResultDataAccessException e) {
            return HttpUtil.responseWithHeaders(HttpStatus.NOT_FOUND, EQUIPMENT_ERROR_CAUSE, e);
        } catch (ActiveProductionOrderException e) {
            return HttpUtil.responseWithHeaders(HttpStatus.CONFLICT, EQUIPMENT_ERROR_CAUSE, e);
        } catch (MesMqttException e) {
            return HttpUtil.responseWithHeaders(HttpStatus.CONFLICT, PLC_ERROR_CAUSE, e);
        }
    }
}
