package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.model.dto.HitDto;
import com.alcegory.mescloud.model.dto.ProductionOrderDto;
import com.alcegory.mescloud.model.request.RequestById;
import com.alcegory.mescloud.model.request.RequestHitDto;
import com.alcegory.mescloud.service.HitService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/hit")
@AllArgsConstructor
public class HitController {

    private final HitService hitService;

    @PostMapping
    public ResponseEntity<List<HitDto>> create(@RequestBody RequestHitDto requestHitDto) {
        List<HitDto> createdHits = hitService.create(requestHitDto);
        if (createdHits.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(createdHits, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<HitDto>> findAll() {
        List<HitDto> hitDtos = hitService.getAll();
        return new ResponseEntity<>(hitDtos, HttpStatus.OK);
    }

    @PostMapping("/remove")
    public ResponseEntity<List<ProductionOrderDto>> removeHits(@RequestBody RequestById request) {
        if (request == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            List<ProductionOrderDto> productionOrders = hitService.removeHits(request);
            return ResponseEntity.ok(productionOrders);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
}