package com.alcegory.mescloud.azure.controller;

import com.alcegory.mescloud.azure.dto.ContainerInfoDto;
import com.alcegory.mescloud.azure.dto.ImageAnnotationDto;
import com.alcegory.mescloud.azure.service.ApprovedContainerService;
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
    private final ApprovedContainerService approvedContainerService;


    @GetMapping("/jpeg")
    public ResponseEntity<List<ContainerInfoDto>> getJpegImages() {
        List<ContainerInfoDto> imageUrls = publicContainerService.getImageAnnotations();
        return new ResponseEntity<>(imageUrls, HttpStatus.OK);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ContainerInfoDto>> getPendingContainerInfo() {
        List<ContainerInfoDto> imageUrls = pendingContainerService.getPendingImageAnnotations();
        return new ResponseEntity<>(imageUrls, HttpStatus.OK);
    }

    @GetMapping("/approved")
    public ResponseEntity<List<ContainerInfoDto>> getApprovedContainerInfo() {
        List<ContainerInfoDto> imageUrls = approvedContainerService.getApprovedImageAnnotations();
        return new ResponseEntity<>(imageUrls, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<ContainerInfoDto> updateImageInfo(@RequestBody ImageAnnotationDto imageAnnotationDto) {
        ContainerInfoDto updatedImage = pendingContainerService.processUpdateImage(imageAnnotationDto);
        return new ResponseEntity<>(updatedImage, HttpStatus.OK);
    }
}
