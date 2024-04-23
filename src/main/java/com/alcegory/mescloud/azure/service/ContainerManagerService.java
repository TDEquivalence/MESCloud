package com.alcegory.mescloud.azure.service;

import com.alcegory.mescloud.azure.model.dto.ContainerInfoSummary;
import com.alcegory.mescloud.azure.model.dto.ContainerInfoUpdate;
import com.alcegory.mescloud.azure.model.dto.ImageAnnotationDto;
import org.springframework.security.core.Authentication;

public interface ContainerManagerService {
    
    ContainerInfoSummary getRandomData(Authentication authentication);

    ImageAnnotationDto processSaveToApprovedContainer(ContainerInfoUpdate containerInfoUpdate, Authentication authentication);
}
