package com.alcegory.mescloud.azure.service;

import com.alcegory.mescloud.azure.dto.ContainerInfoDto;
import com.alcegory.mescloud.azure.dto.ImageAnnotationDto;
import com.alcegory.mescloud.azure.dto.ImageInfoDto;

import java.util.List;

public interface ApprovedContainerService {

    List<ContainerInfoDto> getApprovedImageAnnotations();

    List<ImageInfoDto> getAllImageReference();

    ImageAnnotationDto saveToApprovedContainer(ImageAnnotationDto imageAnnotationDto);
}
