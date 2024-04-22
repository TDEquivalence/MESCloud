package com.alcegory.mescloud.azure.service;

import com.alcegory.mescloud.azure.model.dto.ImageAnnotationDto;

public interface ImageAnnotationService {

    void saveImageAnnotation(ImageAnnotationDto imageAnnotationDto);
}
