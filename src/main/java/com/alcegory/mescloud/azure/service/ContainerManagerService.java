package com.alcegory.mescloud.azure.service;

import com.alcegory.mescloud.azure.dto.ContainerInfoSummary;
import com.alcegory.mescloud.azure.dto.ContainerInfoUpdate;
import com.alcegory.mescloud.azure.dto.ImageAnnotationDto;
import org.springframework.security.core.Authentication;

public interface ContainerManagerService {

    ContainerInfoSummary getData();

    ImageAnnotationDto processSaveToApprovedContainer(ContainerInfoUpdate containerInfoUpdate, Authentication authentication);
}
