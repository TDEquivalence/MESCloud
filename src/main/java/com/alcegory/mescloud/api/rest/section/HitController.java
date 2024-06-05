package com.alcegory.mescloud.api.rest.section;

import com.alcegory.mescloud.api.rest.base.SectionBaseController;
import com.alcegory.mescloud.exception.ForbiddenAccessException;
import com.alcegory.mescloud.model.dto.composed.HitDto;
import com.alcegory.mescloud.model.dto.production.ProductionOrderDto;
import com.alcegory.mescloud.model.request.RequestById;
import com.alcegory.mescloud.model.request.RequestHitDto;
import com.alcegory.mescloud.service.composed.HitService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@AllArgsConstructor
public class HitController extends SectionBaseController {

    private static final String HIT_URL = "/hit";

    private final HitService hitService;

    @PostMapping(HIT_URL)
    public ResponseEntity<List<HitDto>> create(@PathVariable long sectionId, @RequestBody RequestHitDto requestHitDto,
                                               Authentication authentication) {
        try {
            List<HitDto> createdHits = hitService.create(sectionId, requestHitDto, authentication);

            if (createdHits.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(createdHits);
        } catch (ForbiddenAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping(HIT_URL)
    public ResponseEntity<List<HitDto>> findAll() {
        try {
            List<HitDto> hitDtos = hitService.getAll();
            return ResponseEntity.ok(hitDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(HIT_URL + "/remove")
    public ResponseEntity<List<ProductionOrderDto>> removeHits(@PathVariable long sectionId, @RequestBody RequestById request,
                                                               Authentication authentication) {
        if (request == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            List<ProductionOrderDto> productionOrders = hitService.removeHits(sectionId, request, authentication);
            return ResponseEntity.ok(productionOrders);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (ForbiddenAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
}