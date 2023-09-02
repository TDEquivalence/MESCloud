package com.tde.mescloud.api.rest;

import com.tde.mescloud.model.dto.ImsDto;
import com.tde.mescloud.service.ImsServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/ims")
@AllArgsConstructor
public class ImsController {

    private ImsServiceImpl imsService;

    @PostMapping()
    public ResponseEntity<ImsDto> create(@RequestBody ImsDto imsDto) {
        Optional<ImsDto> persistedDtoOpt = imsService.create(imsDto);
        if (persistedDtoOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(persistedDtoOpt.get(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ImsDto>> findAll() {
        List<ImsDto> imsDtos = imsService.getAll();
        return new ResponseEntity<>(imsDtos, HttpStatus.OK);
    }
}
