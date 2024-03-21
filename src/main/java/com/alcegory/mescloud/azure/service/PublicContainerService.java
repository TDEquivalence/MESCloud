package com.alcegory.mescloud.azure.service;

import com.alcegory.mescloud.azure.dto.ContainerInfoDto;

import java.util.List;

public interface PublicContainerService {

    List<ContainerInfoDto> getImageAnnotations();
}
