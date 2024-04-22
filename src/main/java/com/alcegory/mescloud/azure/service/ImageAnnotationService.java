package com.alcegory.mescloud.azure.service;

import com.alcegory.mescloud.azure.model.dto.ImageAnnotationDto;
import org.springframework.security.core.Authentication;

public interface ImageAnnotationService {

    void saveImageAnnotation(ImageAnnotationDto imageAnnotationDto, Authentication authentication);

    void saveApprovedImageAnnotation(ImageAnnotationDto imageAnnotationDto, boolean isApproved, Authentication authentication);
}
