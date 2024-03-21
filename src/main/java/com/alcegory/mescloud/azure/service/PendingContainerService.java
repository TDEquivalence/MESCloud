package com.alcegory.mescloud.azure.service;

import com.alcegory.mescloud.azure.dto.ImageAnnotationDto;

import java.util.List;

public interface PendingContainerService {

    List<ImageAnnotationDto> editImageDecision(ImageAnnotationDto imageAnnotationDto);
}
