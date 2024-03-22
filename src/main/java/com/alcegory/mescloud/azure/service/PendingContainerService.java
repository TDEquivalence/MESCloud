package com.alcegory.mescloud.azure.service;

import com.alcegory.mescloud.azure.dto.ContainerInfoDto;
import com.alcegory.mescloud.azure.dto.ImageAnnotationDto;

import java.util.List;

public interface PendingContainerService {

    ContainerInfoDto processUpdateImage(ImageAnnotationDto updatedImageAnnotationDto);

    List<ContainerInfoDto> getPendingImageAnnotations();
}
