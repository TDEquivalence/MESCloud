package com.tde.mescloud.api.rest;

import com.tde.mescloud.service.ProductionOrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/production-orders")
@AllArgsConstructor
public class ProductionOrderController {

    private ProductionOrderService productionOrderService;

    @GetMapping("/generate-code")
    public ResponseEntity<String> generateCode() {
        String newCode = productionOrderService.generateCode();
        return new ResponseEntity<>(newCode, HttpStatus.OK);
    }
}
