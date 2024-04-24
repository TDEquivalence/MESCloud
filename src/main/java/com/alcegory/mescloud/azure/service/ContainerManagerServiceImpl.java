package com.alcegory.mescloud.azure.service;

import com.alcegory.mescloud.azure.model.dto.*;
import com.alcegory.mescloud.model.entity.UserEntity;
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
                                       ImageAnnotationService imageAnnotationService) {
        this.publicContainerService = publicContainerService;
        this.pendingContainerService = pendingContainerService;
        this.approvedContainerService = approvedContainerService;
        this.imageAnnotationService = imageAnnotationService;
    }

    @Override
    public ImageAnnotationDto getRandomData(Authentication authentication) {
        ImageAnnotationDto imageAnnotationDto;
        do {
            ImageInfoDto imageInfoDto = publicContainerService.getRandomImageReference();
            if (imageInfoDto == null) {
                return new ImageAnnotationDto();
            }

            imageAnnotationDto = pendingContainerService.getImageAnnotationFromContainer(imageInfoDto.getPath());

            if (imageAnnotationDto == null) {
                log.info("The image at path '{}' was not found in the pending container and has been successfully deleted.",
                        imageInfoDto.getPath());
                return new ImageAnnotationDto();
            }
        } while (hasUserDecisionOnImage(imageAnnotationDto, authentication));

        return imageAnnotationDto;
    }

    private boolean hasUserDecisionOnImage(ImageAnnotationDto imageAnnotationDto, Authentication authentication) {
        if (imageAnnotationDto == null || imageAnnotationDto.getData() == null || imageAnnotationDto.getData().getImage() == null) {
            return false;
        }

        UserEntity user = (UserEntity) authentication.getPrincipal();
        return imageAnnotationService.existsByUserIdAndImage(user.getId(), imageAnnotationDto.getData().getImage());
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
        
        boolean isApproved = containerInfoDto.getImageAnnotationDto().isUserApproval();
        imageAnnotationService.saveApprovedImageAnnotation(uploadedImageAnnotationDto, isApproved, authentication);

        String image = containerInfoDto.getImageAnnotationDto().getData().getImage();
        int imageOccurrencesNotInitial = imageAnnotationService.countByImageAndStatusNotInitial(image);
        if (uploadedImageAnnotationDto != null && imageOccurrencesNotInitial >= 3) {
            publicContainerService.deleteBlob(image);
            pendingContainerService.deleteJpgAndJsonBlobs(image);
        }

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
        imageAnnotationDto.setMesUserDecision(containerInfoUpdate.getMesUserDecision());

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
