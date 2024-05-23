package com.alcegory.mescloud.azure.service;

import com.alcegory.mescloud.azure.model.dto.ContainerInfoDto;
import com.alcegory.mescloud.azure.model.dto.ImageAnnotationDto;
import com.alcegory.mescloud.azure.model.dto.ImageInfoDto;

import java.util.List;

public interface ApprovedContainerService {

    List<ContainerInfoDto> getApprovedImageAnnotations();

    List<ImageInfoDto> getAllImageReference();

    ImageAnnotationDto saveToApprovedContainer(ImageAnnotationDto imageAnnotationDto, int imageOccurrencesNotInitial);
}
