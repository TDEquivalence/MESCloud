package com.tde.mescloud.api.rest;

import com.tde.mescloud.model.dto.ComposedProductionOrderDto;
import com.tde.mescloud.model.dto.ProductionOrderDto;
import com.tde.mescloud.service.ComposedProductionOrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/composed-production-order")
@AllArgsConstructor
public class ComposedProductionOrderController {

    private final ComposedProductionOrderService composedProductionOrderService;

    @PostMapping
    public ResponseEntity<ComposedProductionOrderDto> create(@RequestBody ProductionOrderDto[] requestComposedProductionDto) {
        Optional<ComposedProductionOrderDto> composedProductionOpt = composedProductionOrderService.create(requestComposedProductionDto);
        if (composedProductionOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(composedProductionOpt.get(), HttpStatus.OK);
    }
}