package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.model.dto.ImsDto;
import com.alcegory.mescloud.service.ImsServiceImpl;
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
        ImsDto createdIms = imsService.create(imsDto);
        return new ResponseEntity<>(createdIms, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ImsDto>> findAll() {
        List<ImsDto> imsDtos = imsService.getAll();
        return new ResponseEntity<>(imsDtos, HttpStatus.OK);
    }
}
