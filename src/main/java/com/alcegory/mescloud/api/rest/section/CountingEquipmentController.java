package com.alcegory.mescloud.api.rest.section;

import com.alcegory.mescloud.api.rest.base.SectionBaseController;
import com.alcegory.mescloud.exception.*;
import com.alcegory.mescloud.model.dto.equipment.CountingEquipmentDto;
import com.alcegory.mescloud.model.dto.equipment.CountingEquipmentInfoDto;
import com.alcegory.mescloud.model.dto.equipment.TemplateDto;
import com.alcegory.mescloud.model.request.RequestById;
import com.alcegory.mescloud.model.request.RequestConfigurationDto;
import com.alcegory.mescloud.service.equipment.CountingEquipmentService;
import com.alcegory.mescloud.service.management.CountingEquipmentManagementService;
import com.alcegory.mescloud.service.management.ManagementInfoService;
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
@AllArgsConstructor
public class CountingEquipmentController extends SectionBaseController {

    private static final String COUNTING_EQUIPMENT_URL = "/counting-equipments";

    private static final String IMS_ERROR_CAUSE = "IMS";
    private static final String EQUIPMENT_ERROR_CAUSE = "EQUIPMENT";
    private static final String CONFIG_ERROR_CAUSE = "CONFIG";
    private static final String PLC_ERROR_CAUSE = "PLC";

    private final CountingEquipmentService service;
    private final CountingEquipmentManagementService countingEquipmentManagementService;
    private final ManagementInfoService managementInfoService;

    @GetMapping(COUNTING_EQUIPMENT_URL)
    public ResponseEntity<List<CountingEquipmentDto>> findAll(@PathVariable long sectionId) {
        try {
            List<CountingEquipmentDto> countingEquipments = service.findAllWithLastProductionOrder(sectionId);
            if (countingEquipments.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(countingEquipments, HttpStatus.OK);
        } catch (EquipmentNotFoundException e) {
            return HttpUtil.responseWithHeaders(HttpStatus.NOT_FOUND, EQUIPMENT_ERROR_CAUSE, e);
        }
    }

    @GetMapping(COUNTING_EQUIPMENT_URL + "/{id}/info")
    public ResponseEntity<CountingEquipmentInfoDto> findCountingInfoById(@PathVariable long id) {
        try {
            Optional<CountingEquipmentInfoDto> countingEquipmentOpt = managementInfoService.findEquipmentWithProductionOrderById(id);
            if (countingEquipmentOpt.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(countingEquipmentOpt.get(), HttpStatus.OK);
        } catch (EquipmentNotFoundException e) {
            return HttpUtil.responseWithHeaders(HttpStatus.NOT_FOUND, EQUIPMENT_ERROR_CAUSE, e);
        }
    }

    @GetMapping(COUNTING_EQUIPMENT_URL + "/{id}")
    public ResponseEntity<CountingEquipmentDto> findById(@PathVariable long id) {
        try {
            CountingEquipmentDto countingEquipmentOpt = managementInfoService.findEquipmentById(id);
            if (countingEquipmentOpt == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(countingEquipmentOpt, HttpStatus.OK);
        } catch (EquipmentNotFoundException e) {
            return HttpUtil.responseWithHeaders(HttpStatus.NOT_FOUND, EQUIPMENT_ERROR_CAUSE, e);
        }
    }

    @GetMapping(COUNTING_EQUIPMENT_URL + "/{id}/template")
    public ResponseEntity<TemplateDto> findTemplateById(@PathVariable long id) {
        try {
            TemplateDto templateDto = countingEquipmentManagementService.findEquipmentTemplate(id);
            if (templateDto == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(templateDto, HttpStatus.OK);
        } catch (EquipmentNotFoundException e) {
            return HttpUtil.responseWithHeaders(HttpStatus.NOT_FOUND, EQUIPMENT_ERROR_CAUSE, e);
        }
    }

    @PutMapping(COUNTING_EQUIPMENT_URL + "/{equipmentId}/ims")
    public ResponseEntity<CountingEquipmentDto> updateIms(@PathVariable long equipmentId, @RequestBody RequestById request,
                                                          Authentication authentication) {
        try {
            CountingEquipmentDto updatedIms = countingEquipmentManagementService.updateIms(equipmentId, request.getId(), authentication);
            return new ResponseEntity<>(updatedIms, HttpStatus.OK);
        } catch (EquipmentNotFoundException e) {
            return HttpUtil.responseWithHeaders(HttpStatus.NOT_FOUND, IMS_ERROR_CAUSE, e);
        } catch (ImsNotFoundException e) {
            return HttpUtil.responseWithHeaders(HttpStatus.NOT_FOUND, EQUIPMENT_ERROR_CAUSE, e);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (ForbiddenAccessException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping(COUNTING_EQUIPMENT_URL + "/{equipmentId}/configuration")
    public ResponseEntity<CountingEquipmentDto> updateConfiguration(@PathVariable String companyPrefix, @PathVariable String sectionPrefix,
                                                                    @PathVariable long sectionId, @PathVariable long equipmentId,
                                                                    @RequestBody RequestConfigurationDto request,
                                                                    Authentication authentication) {
        try {
            CountingEquipmentDto countingEquipment = countingEquipmentManagementService.updateConfiguration(companyPrefix, sectionPrefix,
                    sectionId, equipmentId, request, authentication);
            return new ResponseEntity<>(countingEquipment, HttpStatus.OK);
        } catch (IncompleteConfigurationException e) {
            return HttpUtil.responseWithHeaders(HttpStatus.BAD_REQUEST, CONFIG_ERROR_CAUSE, e);
        } catch (EmptyResultDataAccessException e) {
            return HttpUtil.responseWithHeaders(HttpStatus.NOT_FOUND, EQUIPMENT_ERROR_CAUSE, e);
        } catch (ActiveProductionOrderException e) {
            return HttpUtil.responseWithHeaders(HttpStatus.CONFLICT, EQUIPMENT_ERROR_CAUSE, e);
        } catch (MesMqttException e) {
            return HttpUtil.responseWithHeaders(HttpStatus.CONFLICT, PLC_ERROR_CAUSE, e);
        } catch (ForbiddenAccessException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
