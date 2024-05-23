package com.alcegory.mescloud.azure.service;

import com.alcegory.mescloud.azure.model.dto.*;
import com.alcegory.mescloud.exception.ImageAnnotationException;
import com.alcegory.mescloud.model.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ContainerManagerServiceImpl implements ContainerManagerService {

    public static final int MAX_ITERATIONS = 5;
    public static final int MAX_OCCURRENCES = 3;

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
                log.error("Image reference is null.");
                throw new ImageAnnotationException("Image reference is null.");
            }

            imageAnnotationDto = pendingContainerService.getImageAnnotationFromContainer(imageInfoDto.getPath());

            if (imageAnnotationDto == null) {
                log.info("The image at path '{}' was not found in the pending container.",
                        imageInfoDto.getPath());
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
        validateContainerInfoUpdate(containerInfoUpdate);

        ImageAnnotationDto imageAnnotationDto = updateImageAnnotation(containerInfoUpdate);
        ContainerInfoDto containerInfoDto = convertToContainerInfo(imageAnnotationDto, containerInfoUpdate);

        if (containerInfoDto == null) {
            throw new IllegalArgumentException("ContainerInfoUpdate or FileName cannot be null");
        }

        containerInfoDto.getImageAnnotationDto().setUsername(authentication.getName());

        return saveToApprovedContainer(containerInfoDto, authentication);
    }

    private void validateContainerInfoUpdate(ContainerInfoUpdate containerInfoUpdate) {
        if (containerInfoUpdate == null || containerInfoUpdate.getFileName() == null) {
            throw new IllegalArgumentException("ContainerInfoUpdate or FileName cannot be null");
        }
    }

    public ImageAnnotationDto updateImageAnnotation(ContainerInfoUpdate containerInfoUpdate) {
        ImageAnnotationDto imageAnnotationDto = pendingContainerService.getImageAnnotationFromContainer(containerInfoUpdate.getFileName());

        if (imageAnnotationDto == null) {
            throw new ImageAnnotationException("ImageAnnotationDto is null. The image might have been deleted from the pending container.");
        }

        if (containerInfoUpdate.getAnnotations() != null) {
            imageAnnotationDto.setAnnotations(containerInfoUpdate.getAnnotations());
        }

        return imageAnnotationDto;
    }

    private ImageAnnotationDto saveToApprovedContainer(ContainerInfoDto containerInfoDto, Authentication authentication) {
        if (containerInfoDto == null || containerInfoDto.getImageAnnotationDto() == null) {
            return null;
        }

        String image = containerInfoDto.getImageAnnotationDto().getData().getImage();
        int imageOccurrencesNotInitial = imageAnnotationService.countByImageAndStatusNotInitial(image);

        saveInitialApprovedImageAnnotation(containerInfoDto, authentication, imageOccurrencesNotInitial);

        ImageAnnotationDto uploadedImageAnnotationDto = approvedContainerService.saveToApprovedContainer(containerInfoDto.getImageAnnotationDto(),
                imageOccurrencesNotInitial);

        saveApprovedImageAnnotation(uploadedImageAnnotationDto, containerInfoDto.getImageAnnotationDto().isUserApproval(), authentication);
        handleImageOccurrences(uploadedImageAnnotationDto, image, imageOccurrencesNotInitial);
        return uploadedImageAnnotationDto;
    }

    private void handleImageOccurrences(ImageAnnotationDto uploadedImageAnnotationDto, String image, int imageOccurrencesNotInitial) {
        if (uploadedImageAnnotationDto != null && imageOccurrencesNotInitial >= MAX_OCCURRENCES) {
            deleteBlobsForImage(image);
        }
    }

    private void saveInitialApprovedImageAnnotation(ContainerInfoDto containerInfoDto, Authentication authentication, int imageOccurrencesNotInitial) {
        if (imageOccurrencesNotInitial == 0) {
            imageAnnotationService.saveImageAnnotation(containerInfoDto.getImageAnnotationDto(), authentication);
        }
    }

    private void saveApprovedImageAnnotation(ImageAnnotationDto uploadedImageAnnotationDto, boolean isApproved, Authentication authentication) {
        if (uploadedImageAnnotationDto != null) {
            imageAnnotationService.saveApprovedImageAnnotation(uploadedImageAnnotationDto, isApproved, authentication);
        }
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
