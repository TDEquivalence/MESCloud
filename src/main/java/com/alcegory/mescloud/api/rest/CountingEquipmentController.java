package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.exception.ActiveProductionOrderException;
import com.alcegory.mescloud.exception.IncompleteConfigurationException;
import com.alcegory.mescloud.model.dto.CountingEquipmentDto;
import com.alcegory.mescloud.model.dto.RequestConfigurationDto;
import com.alcegory.mescloud.service.CountingEquipmentService;
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
    public ResponseEntity<CountingEquipmentDto> updateIms(@PathVariable long equipmentId, @RequestBody Long imsId) {
        Optional<CountingEquipmentDto> updatedIms = service.updateIms(equipmentId, imsId);
        if (updatedIms.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(updatedIms.get(), HttpStatus.OK);
    }

    @PutMapping("/{equipmentId}/configuration")
    public ResponseEntity<CountingEquipmentDto> updateConfiguration(@PathVariable long equipmentId,
                                                                    @RequestBody RequestConfigurationDto request) {
        try {

            CountingEquipmentDto countingEquipment = service.updateConfiguration(equipmentId, request);
            return new ResponseEntity<>(countingEquipment, HttpStatus.OK);

        } catch (IncompleteConfigurationException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ActiveProductionOrderException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
}
