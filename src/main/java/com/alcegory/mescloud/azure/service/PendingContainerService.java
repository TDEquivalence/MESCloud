package com.alcegory.mescloud.azure.service;

import com.alcegory.mescloud.azure.dto.ContainerInfoDto;
import com.alcegory.mescloud.azure.dto.ImageAnnotationDto;
import com.alcegory.mescloud.azure.dto.ImageInfoDto;

import java.util.List;

public interface PendingContainerService {

    List<ContainerInfoDto> getPendingImageAnnotations();

    ContainerInfoDto getImageAnnotationDtoByImageInfo(ImageInfoDto imageInfoDto);

    ImageAnnotationDto getImageAnnotationFromContainer(String imageUrl);

    void deleteJpgAndJsonBlobs(String blobName);
}
