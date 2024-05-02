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
    public ContainerInfoSummary getRandomData(Authentication authentication) {
        ImageAnnotationDto imageAnnotationDto;
        ImageInfoDto imageInfoDto;
        do {
            imageInfoDto = publicContainerService.getRandomImageReference();
            if (imageInfoDto == null) {
                return new ContainerInfoSummary();
            }

            imageAnnotationDto = pendingContainerService.getImageAnnotationFromContainer(imageInfoDto.getPath());

            if (imageAnnotationDto == null) {
                log.info("The image at path '{}' was not found in the pending container.",
                        imageInfoDto.getPath());
                return new ContainerInfoSummary();
            }
        } while (hasUserDecisionOnImage(imageAnnotationDto, authentication));

        return convertToSummary(imageAnnotationDto, imageInfoDto);
    }

    private boolean hasUserDecisionOnImage(ImageAnnotationDto imageAnnotationDto, Authentication authentication) {
        if (imageAnnotationDto == null || imageAnnotationDto.getData() == null || imageAnnotationDto.getData().getImage() == null) {
            return false;
        }

        UserEntity user = (UserEntity) authentication.getPrincipal();
        return imageAnnotationService.existsByUserIdAndImage(user.getId(), imageAnnotationDto.getData().getImage());
    }

    @Override
    public ImageAnnotationDto processSaveToApprovedContainer(ContainerInfoUpdate containerInfoUpdate,
                                                             Authentication authentication) {
        if (containerInfoUpdate == null || containerInfoUpdate.getFileName() == null) {
            throw new IllegalArgumentException("ContainerInfoUpdate or FileName cannot be null");
        }

        ImageAnnotationDto imageAnnotationDto = updateAndSaveImageAnnotation(containerInfoUpdate, authentication);
        ContainerInfoDto containerInfoDto = convertToContainerInfo(imageAnnotationDto, containerInfoUpdate);

        if (authentication != null && authentication.getName() != null && containerInfoDto != null) {
            containerInfoDto.getImageAnnotationDto().setUsername(authentication.getName());
        } else {
            throw new IllegalStateException("Authentication or Username is null");
        }

        return saveToApprovedContainer(containerInfoDto, authentication);
    }

    public ImageAnnotationDto updateAndSaveImageAnnotation(ContainerInfoUpdate containerInfoUpdate,
                                                           Authentication authentication) {
        ImageAnnotationDto imageAnnotationDto =
                pendingContainerService.getImageAnnotationFromContainer(containerInfoUpdate.getFileName());

        if (imageAnnotationDto == null) {
            throw new IllegalStateException("ImageAnnotationDto is null");
        }

        if (containerInfoUpdate.getAnnotations() != null) {
            imageAnnotationDto.setAnnotations(containerInfoUpdate.getAnnotations());
        }

        imageAnnotationService.saveImageAnnotation(imageAnnotationDto, authentication);
        return imageAnnotationDto;
    }

    private ImageAnnotationDto saveToApprovedContainer(ContainerInfoDto containerInfoDto, Authentication authentication) {
        if (containerInfoDto == null || containerInfoDto.getImageAnnotationDto() == null) {
            return null;
        }

        ImageAnnotationDto uploadedImageAnnotationDto = approvedContainerService.saveToApprovedContainer(containerInfoDto.getImageAnnotationDto());

        boolean isApproved = containerInfoDto.getImageAnnotationDto().isUserApproval();
        ImageAnnotationDto uploadedImageAnnotation = checkImageOccurrencesToRemove(containerInfoDto, uploadedImageAnnotationDto);
        imageAnnotationService.saveApprovedImageAnnotation(uploadedImageAnnotationDto, isApproved, authentication);

        return uploadedImageAnnotation;
    }

    private ImageAnnotationDto checkImageOccurrencesToRemove(ContainerInfoDto containerInfoDto, ImageAnnotationDto uploadedImageAnnotationDto) {
        if (containerInfoDto == null || containerInfoDto.getImageAnnotationDto() == null) {
            return null;
        }

        String image = containerInfoDto.getImageAnnotationDto().getData().getImage();
        if (image == null) {
            return null;
        }

        int imageOccurrencesNotInitial = imageAnnotationService.countByImageAndStatusNotInitial(image);
        if (uploadedImageAnnotationDto != null && imageOccurrencesNotInitial >= 3) {
            deleteBlobsForImage(image);
        }

        if (uploadedImageAnnotationDto != null && imageOccurrencesNotInitial != 0) {
            String imageDataOccurrence = image + "_" + imageOccurrencesNotInitial;
            uploadedImageAnnotationDto.getData().setImage(imageDataOccurrence);
        }

        return uploadedImageAnnotationDto;
    }

    private void deleteBlobsForImage(String image) {
        publicContainerService.deleteBlob(image);
        pendingContainerService.deleteJpgAndJsonBlobs(image);
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

    private ContainerInfoSummary convertToSummary(ImageAnnotationDto imageAnnotationDto, ImageInfoDto imageInfoDto) {
        if (imageAnnotationDto == null) {
            return null;
        }

        ContainerInfoSummary summary = new ContainerInfoSummary();
        summary.setImageAnnotationDto(imageAnnotationDto);
        summary.setSasToken(publicContainerService.getSasToken());
        summary.setPath(imageInfoDto.getPath());

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
