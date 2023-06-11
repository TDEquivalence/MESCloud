package com.tde.mescloud.api.rest;

import com.tde.mescloud.model.ProductionOrder;
import com.tde.mescloud.model.converter.ProductionOrderConverter;
import com.tde.mescloud.model.dto.ProductionOrderDto;
import com.tde.mescloud.service.ProductionOrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/production-orders")
@AllArgsConstructor
public class ProductionOrderController {

    private ProductionOrderService productionOrderService;
    private ProductionOrderConverter productionOrderConverter;

    @GetMapping("/generate-code")
    public ResponseEntity<String> generateCode() {
        String newCode = productionOrderService.generateCode();
        return new ResponseEntity<>(newCode, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ProductionOrderDto> save(@RequestBody ProductionOrderDto requestProductionOrder) {
        ProductionOrder productionOrder = productionOrderService.save(requestProductionOrder);
        ProductionOrderDto responseProductionOrder = productionOrderConverter.convertToDto(productionOrder);
        return new ResponseEntity<>(responseProductionOrder, HttpStatus.OK);
    }
}