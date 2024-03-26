package com.alcegory.mescloud.azure.service;

import com.alcegory.mescloud.azure.dto.ContainerInfoDto;
import com.alcegory.mescloud.azure.dto.ImageAnnotationDto;

public interface ContainerManagerService {

    ContainerInfoDto getData();

    ImageAnnotationDto saveToApprovedContainer(ContainerInfoDto containerInfoDto);
}
