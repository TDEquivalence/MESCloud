package com.tde.mescloud.api.rest;

import com.tde.mescloud.model.CountingEquipment;
import com.tde.mescloud.model.converter.CountingEquipmentConverter;
import com.tde.mescloud.model.dto.CountingEquipmentDto;
import com.tde.mescloud.service.CountingEquipmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/counting-equipments")
@AllArgsConstructor
public class CountingEquipmentController {

    private CountingEquipmentService countingEquipmentService;
    private CountingEquipmentConverter countingEquipmentConverter;

    @GetMapping
    public ResponseEntity<List<CountingEquipmentDto>> getEquipments() {
        List<CountingEquipment> countingEquipments = countingEquipmentService.findAll();
        List<CountingEquipmentDto> countingEquipmentDtos = countingEquipmentConverter.convertToDto(countingEquipments);
        return new ResponseEntity<>(countingEquipmentDtos, HttpStatus.OK);
    }
}
