package com.tde.mescloud.api.rest;

import com.tde.mescloud.model.dto.HitDto;
import com.tde.mescloud.model.dto.RequestHitDto;
import com.tde.mescloud.service.HitService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}