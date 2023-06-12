package com.tde.mescloud.api.rest;

import com.tde.mescloud.model.CountingEquipment;
import com.tde.mescloud.model.ProductionOrder;
import com.tde.mescloud.model.converter.CountingEquipmentConverter;
import com.tde.mescloud.model.converter.ProductionOrderConverter;
import com.tde.mescloud.model.dto.CountingEquipmentDto;
import com.tde.mescloud.model.dto.ProductionOrderDto;
import com.tde.mescloud.service.CountingEquipmentService;
import com.tde.mescloud.service.ProductionOrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/counting-equipments")
@AllArgsConstructor
public class CountingEquipmentController {

    private CountingEquipmentService countingEquipmentService;
    private CountingEquipmentConverter countingEquipmentConverter;
    private ProductionOrderService productionOrderService;
    private ProductionOrderConverter productionOrderConverter;

    @GetMapping
    public ResponseEntity<List<CountingEquipmentDto>> getEquipments() {
        List<CountingEquipment> countingEquipments = countingEquipmentService.findAll();
        List<CountingEquipmentDto> countingEquipmentDtos = countingEquipmentConverter.convertToDto(countingEquipments);
        return new ResponseEntity<>(countingEquipmentDtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CountingEquipmentDto> findById(@PathVariable long id) {
        Optional<CountingEquipment> countingEquipmentOpt = countingEquipmentService.findById(id);
        if (countingEquipmentOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        CountingEquipment countingEquipment = countingEquipmentOpt.get();
        CountingEquipmentDto countingEquipmentDto = countingEquipmentConverter.convertToDto(countingEquipment);

        boolean hasActiveProductionOrder = productionOrderService.hasActiveProductionOrder(countingEquipment.getId());
        countingEquipmentDto.setHasActiveProductionOrder(hasActiveProductionOrder);

        return new ResponseEntity<>(countingEquipmentDto, HttpStatus.OK);
    }
}
