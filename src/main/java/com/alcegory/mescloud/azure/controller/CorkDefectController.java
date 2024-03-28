package com.alcegory.mescloud.azure.controller;

import com.alcegory.mescloud.azure.dto.*;
import com.alcegory.mescloud.azure.service.ApprovedContainerService;
import com.alcegory.mescloud.azure.service.ContainerManagerService;
import com.alcegory.mescloud.azure.service.PublicContainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class CorkDefectController {

    private final PublicContainerService publicContainerService;
    private final ContainerManagerService containerManagerService;

    @GetMapping("/all-image-reference")
    public ResponseEntity<List<ImageInfoDto>> getImageAllReference() {
        List<ImageInfoDto> imageUrls = publicContainerService.getAllImageReference();
        return new ResponseEntity<>(imageUrls, HttpStatus.OK);
    }

    @GetMapping("/corkDetails")
    public ResponseEntity<ContainerInfoSummary> getImageAnnotation() {
        ContainerInfoSummary containerInfoSummary = containerManagerService.getData();
        return new ResponseEntity<>(containerInfoSummary, HttpStatus.OK);
    }

    @PostMapping("/updateCorkDetails")
    public ResponseEntity<ImageAnnotationDto> updateImageAnnotation(@RequestBody ContainerInfoUpdate containerInfoUpdate) {
        ImageAnnotationDto updatedImageAnnotationDto = containerManagerService.processSaveToApprovedContainer(containerInfoUpdate);
        return new ResponseEntity<>(updatedImageAnnotationDto, HttpStatus.OK);
    }
}
