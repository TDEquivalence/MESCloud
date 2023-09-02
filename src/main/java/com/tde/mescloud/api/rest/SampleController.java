package com.tde.mescloud.api.rest;

import com.tde.mescloud.model.dto.RequestSampleDto;
import com.tde.mescloud.model.dto.filter.SampleDto;
import com.tde.mescloud.service.SampleService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/sample")
@AllArgsConstructor
public class SampleController {

    private final SampleService sampleService;

    @PostMapping
    public ResponseEntity<SampleDto> createSample(@RequestBody RequestSampleDto requestSampleDto) {
        Optional<SampleDto> sampleDto = sampleService.create(requestSampleDto);
        if (sampleDto.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(sampleDto.get(), HttpStatus.OK);
    }
}