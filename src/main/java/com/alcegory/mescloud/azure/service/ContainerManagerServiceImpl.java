package com.alcegory.mescloud.azure.service;

import com.alcegory.mescloud.azure.dto.ContainerInfoDto;
import com.alcegory.mescloud.azure.dto.ImageAnnotationDto;
import com.alcegory.mescloud.azure.dto.ImageInfoDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ContainerManagerServiceImpl implements ContainerManagerService {

    private PublicContainerService publicContainerService;
    private PendingContainerService pendingContainerService;
    private ApprovedContainerService approvedContainerService;

    @Override
    public ContainerInfoDto getData() {
        ImageInfoDto imageInfoDto = publicContainerService.getImageReference();
        ImageAnnotationDto imageAnnotationDto = pendingContainerService.getImageAnnotationFromContainer(imageInfoDto);

        ContainerInfoDto containerInfoDto = new ContainerInfoDto();
        containerInfoDto.setJpg(imageInfoDto);
        containerInfoDto.setImageAnnotationDto(imageAnnotationDto);
        return containerInfoDto;
    }

    @Override
    public ImageAnnotationDto saveToApprovedContainer(ContainerInfoDto containerInfoDto) {
        //TODO: delete json and jpeg from public and pending containers
        ImageAnnotationDto uploadedImageAnnotationDto = approvedContainerService.saveToApprovedContainer(containerInfoDto.getImageAnnotationDto());

        publicContainerService.deleteBlob(containerInfoDto.getJpg().getPath());
        pendingContainerService.deleteJpgAndJsonBlobs(containerInfoDto.getJpg().getPath());
        return uploadedImageAnnotationDto;
    }
}
