package com.alcegory.mescloud.azure.controller;

import com.alcegory.mescloud.api.rest.response.ErrorResponse;
import com.alcegory.mescloud.azure.model.dto.ContainerInfoSummary;
import com.alcegory.mescloud.azure.model.dto.ContainerInfoUpdate;
import com.alcegory.mescloud.azure.model.dto.ImageAnnotationDto;
import com.alcegory.mescloud.azure.model.dto.ImageInfoDto;
import com.alcegory.mescloud.azure.service.ContainerManagerService;
import com.alcegory.mescloud.azure.service.PublicContainerService;
import com.alcegory.mescloud.exception.ImageAnnotationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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
    public ResponseEntity<Object> getImageAnnotation(Authentication authentication) {
        try {
            ContainerInfoSummary imageAnnotation = containerManagerService.getRandomData(authentication);
            return new ResponseEntity<>(imageAnnotation, HttpStatus.OK);
        } catch (ImageAnnotationException ex) {
            ErrorResponse errorResponse = new ErrorResponse("Internal server error occurred while saving image annotation");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } catch (AuthenticationException ex) {
            ErrorResponse errorResponse = new ErrorResponse("Authentication failed: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @PostMapping("/updateCorkDetails")
    public ResponseEntity<Object> updateImageAnnotation(@RequestBody ContainerInfoUpdate containerInfoUpdate,
                                                        Authentication authentication) {
        try {
            ImageAnnotationDto updatedImageAnnotationDto =
                    containerManagerService.processSaveToApprovedContainer(containerInfoUpdate, authentication);
            return ResponseEntity.ok(updatedImageAnnotationDto);
        } catch (ImageAnnotationException ex) {
            ErrorResponse errorResponse = new ErrorResponse("ImageAnnotationDto is null. " +
                    "The image might have been deleted from the pending container.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
}
