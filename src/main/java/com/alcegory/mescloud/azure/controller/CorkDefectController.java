package com.alcegory.mescloud.azure.controller;

import com.alcegory.mescloud.azure.dto.ContainerInfoDto;
import com.alcegory.mescloud.azure.dto.ImageAnnotationDto;
import com.alcegory.mescloud.azure.service.PendingContainerService;
import com.alcegory.mescloud.azure.service.PublicContainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cork-defect")
public class CorkDefectController {

    private final PublicContainerService publicContainerService;
    private final PendingContainerService pendingContainerService;


    @GetMapping("/jpeg")
    public ResponseEntity<List<ContainerInfoDto>> getJpegImages() {
        List<ContainerInfoDto> imageUrls = publicContainerService.getImageAnnotations();
        return new ResponseEntity<>(imageUrls, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<List<ImageAnnotationDto>> getJpegImages(@RequestBody ImageAnnotationDto imageAnnotationDto) {
        List<ImageAnnotationDto> updatedImages = pendingContainerService.editImageDecision(imageAnnotationDto);
        return new ResponseEntity<>(updatedImages, HttpStatus.OK);
    }
}
