package com.tde.mescloud.api.rest;

import com.tde.mescloud.model.dto.RequestSampleDto;
import com.tde.mescloud.model.dto.filter.SampleDto;
import com.tde.mescloud.service.SampleService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sample")
@AllArgsConstructor
public class SampleController {

    private final SampleService sampleService;

    @PostMapping
<<<<<<< HEAD
    public ResponseEntity<SampleDto> createSample(@RequestBody RequestSampleDto requestSampleDto) {
        Optional<SampleDto> sampleDto = sampleService.create(requestSampleDto);
        if (sampleDto.isEmpty()) {
=======
    public ResponseEntity<SampleDto> create(@RequestBody RequestSampleDto requestSampleDto) {
        SampleDto sampleDto = sampleService.create(requestSampleDto);
        if (sampleDto == null) {
>>>>>>> 721d0ac (Merge branch 'development' into bug/MES-238)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(sampleDto.get(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<SampleDto>> findAll() {
        List<SampleDto> sampleDtos = sampleService.getAll();
        return new ResponseEntity<>(sampleDtos, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<SampleDto>> findAll() {
        List<SampleDto> sampleDtos = sampleService.getAll();
        return new ResponseEntity<>(sampleDtos, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<SampleDto>> findAll() {
        List<SampleDto> sampleDtos = sampleService.getAll();
        return new ResponseEntity<>(sampleDtos, HttpStatus.OK);
    }
}