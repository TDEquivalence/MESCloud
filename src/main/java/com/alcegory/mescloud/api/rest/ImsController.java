package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.model.dto.ImsDto;
import com.alcegory.mescloud.service.equipment.ImsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/ims")
@AllArgsConstructor
public class ImsController {

    private ImsService imsService;

    @PostMapping()
    public ResponseEntity<ImsDto> create(@RequestBody ImsDto imsDto) {
        try {
            if (imsDto == null) {
                return ResponseEntity.badRequest().build();
            }

            ImsDto createdIms = imsService.create(imsDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdIms);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ImsDto>> findAll() {
        try {
            List<ImsDto> imsDtos = imsService.getAll();
            return ResponseEntity.ok(imsDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
