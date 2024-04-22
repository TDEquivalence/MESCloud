package com.alcegory.mescloud.azure.service;

import com.alcegory.mescloud.azure.model.converter.ImageAnnotationConverter;
import com.alcegory.mescloud.azure.model.dto.*;
import com.alcegory.mescloud.azure.repository.AnnotationRepository;
import com.alcegory.mescloud.azure.repository.ImageAnnotationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ContainerManagerServiceImpl implements ContainerManagerService {

    private final PublicContainerService publicContainerService;
    private final PendingContainerService pendingContainerService;
    private final ApprovedContainerService approvedContainerService;

    private final ImageAnnotationService imageAnnotationService;


    public ContainerManagerServiceImpl(PublicContainerService publicContainerService,
                                       PendingContainerService pendingContainerService,
                                       ApprovedContainerService approvedContainerService,
                                       ImageAnnotationRepository imageAnnotationRepository,
                                       AnnotationRepository annotationRepository, ImageAnnotationConverter converter, ImageAnnotationService imageAnnotationService) {
        this.publicContainerService = publicContainerService;
        this.pendingContainerService = pendingContainerService;
        this.approvedContainerService = approvedContainerService;
        this.imageAnnotationService = imageAnnotationService;
    }

    @Override
    public ContainerInfoSummary getData(Authentication authentication) {
        ImageInfoDto imageInfoDto = publicContainerService.getImageReference();
        if (imageInfoDto == null) {
            return new ContainerInfoSummary();
        }

        ImageAnnotationDto imageAnnotationDto =
                pendingContainerService.getImageAnnotationFromContainer(imageInfoDto.getPath());

        if (imageAnnotationDto == null) {
            log.info("The image at path '{}' was not found in the pending container and has been successfully deleted.",
                    imageInfoDto.getPath());
            return new ContainerInfoSummary();
        }

        ContainerInfoDto containerInfoDto = new ContainerInfoDto();
        containerInfoDto.setJpg(imageInfoDto);
        containerInfoDto.setImageAnnotationDto(imageAnnotationDto);

        return convertToSummary(containerInfoDto);
    }

    @Override
    public ContainerInfoSummary getRandomData(Authentication authentication) {
        ImageInfoDto imageInfoDto = publicContainerService.getRandomImageReference();
        if (imageInfoDto == null) {
            return new ContainerInfoSummary();
        }

        ImageAnnotationDto imageAnnotationDto =
                pendingContainerService.getImageAnnotationFromContainer(imageInfoDto.getPath());

        if (imageAnnotationDto == null) {
            log.info("The image at path '{}' was not found in the pending container and has been successfully deleted.",
                    imageInfoDto.getPath());
            return new ContainerInfoSummary();
        }

        ContainerInfoDto containerInfoDto = new ContainerInfoDto();
        containerInfoDto.setJpg(imageInfoDto);
        containerInfoDto.setImageAnnotationDto(imageAnnotationDto);

        return convertToSummary(containerInfoDto);
    }

    @Override
    public ImageAnnotationDto processSaveToApprovedContainer(ContainerInfoUpdate containerInfoUpdate, Authentication authentication) {
        if (containerInfoUpdate == null || containerInfoUpdate.getFileName() == null) {
            throw new IllegalArgumentException("ContainerInfoUpdate or FileName cannot be null");
        }

        ImageAnnotationDto imageAnnotationDto =
                pendingContainerService.getImageAnnotationFromContainer(containerInfoUpdate.getFileName());

        if (imageAnnotationDto == null) {
            throw new IllegalStateException("ImageAnnotationDto is null");
        }

        imageAnnotationService.saveImageAnnotation(imageAnnotationDto, authentication);
        ContainerInfoDto containerInfoDto = convertToContainerInfo(imageAnnotationDto, containerInfoUpdate);

        if (authentication != null && authentication.getName() != null) {
            containerInfoDto.getImageAnnotationDto().setUsername(authentication.getName());
        } else {
            throw new IllegalStateException("Authentication or Username is null");
        }

        return saveToApprovedContainer(containerInfoDto, authentication);
    }

    private ImageAnnotationDto saveToApprovedContainer(ContainerInfoDto containerInfoDto, Authentication authentication) {
        if (containerInfoDto == null || containerInfoDto.getImageAnnotationDto() == null) {
            return null;
        }

        ImageAnnotationDto uploadedImageAnnotationDto =
                approvedContainerService.saveToApprovedContainer(containerInfoDto.getImageAnnotationDto());
        String image = containerInfoDto.getImageAnnotationDto().getData().getImage();
        if (uploadedImageAnnotationDto != null && image != null) {
            publicContainerService.deleteBlob(image);
            pendingContainerService.deleteJpgAndJsonBlobs(image);
        }

        boolean isApproved = containerInfoDto.getImageAnnotationDto().isUserApproval();
        imageAnnotationService.saveApprovedImageAnnotation(uploadedImageAnnotationDto, isApproved, authentication);
        return uploadedImageAnnotationDto;
    }

    private ContainerInfoDto convertToContainerInfo(ImageAnnotationDto imageAnnotationDto,
                                                    ContainerInfoUpdate containerInfoUpdate) {
        if (imageAnnotationDto == null || containerInfoUpdate == null) {
            return null;
        }

        imageAnnotationDto.setClassification(containerInfoUpdate.getClassification());
        imageAnnotationDto.setRejection(containerInfoUpdate.getRejection());
        imageAnnotationDto.setComments(containerInfoUpdate.getComments());
        imageAnnotationDto.setUserApproval(containerInfoUpdate.isUserApproval());

        ContainerInfoDto containerInfoDto = new ContainerInfoDto();
        containerInfoDto.setImageAnnotationDto(imageAnnotationDto);
        return containerInfoDto;
    }

    private ContainerInfoSummary convertToSummary(ContainerInfoDto containerInfoDto) {
        if (containerInfoDto == null || containerInfoDto.getImageAnnotationDto() == null
                || containerInfoDto.getImageAnnotationDto().getData() == null) {
            return null;
        }

        ContainerInfoSummary summary = new ContainerInfoSummary();
        summary.setImageUrl(containerInfoDto.getJpg().getPath());
        summary.setSasToken(publicContainerService.getSasToken());
        summary.setModelDecision(containerInfoDto.getImageAnnotationDto().getModelDecision());
        summary.setAnnotations(getRectangleLabels(containerInfoDto.getImageAnnotationDto().getAnnotations()));

        return summary;
    }

    private List<String> getRectangleLabels(List<AnnotationDto> annotations) {
        List<String> rectangleLabels = new ArrayList<>();
        if (annotations != null) {
            for (AnnotationDto annotation : annotations) {
                addRectangleLabels(annotation.getResult(), rectangleLabels);
            }
        }
        return rectangleLabels;
    }

    private void addRectangleLabels(List<ResultDto> results, List<String> rectangleLabels) {
        if (results != null) {
            for (ResultDto result : results) {
                ValueDto value = result.getValue();
                if (value != null && value.getRectangleLabels() != null) {
                    rectangleLabels.addAll(value.getRectangleLabels());
                }
            }
        }
    }
}
