package com.alcegory.mescloud.azure.service;

import com.alcegory.mescloud.azure.model.dto.ContainerInfoSummary;
import com.alcegory.mescloud.azure.model.dto.ContainerInfoUpdate;
import com.alcegory.mescloud.azure.model.dto.ImageAnnotationDto;
import com.alcegory.mescloud.azure.model.dto.ImageInfoDto;
import com.alcegory.mescloud.exception.ImageAnnotationException;
import com.alcegory.mescloud.model.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

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
            if (iterationCount >= MAX_ITERATIONS) {
                log.info("Maximum number of iterations reached.");
                throw new ImageAnnotationException("There are no more images. Maximum number of iterations reached.");
            }

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

        ImageAnnotationDto originalImageContainerInfoDto = getImageAnnotationFromContainer(containerInfoUpdate);
        ImageAnnotationDto updatedContainerInfoDto = convertToContainerInfo(originalImageContainerInfoDto, containerInfoUpdate);

        if (updatedContainerInfoDto == null) {
            throw new IllegalArgumentException("ContainerInfoUpdate or FileName cannot be null");
        }

        updatedContainerInfoDto.setUsername(authentication.getName());

        return saveToApprovedContainer(updatedContainerInfoDto, originalImageContainerInfoDto, authentication);
    }

    private void validateContainerInfoUpdate(ContainerInfoUpdate containerInfoUpdate) {
        if (containerInfoUpdate == null || containerInfoUpdate.getFileName() == null) {
            throw new IllegalArgumentException("ContainerInfoUpdate or FileName cannot be null");
        }
    }

    public ImageAnnotationDto getImageAnnotationFromContainer(ContainerInfoUpdate containerInfoUpdate) {
        ImageAnnotationDto imageAnnotationDto = pendingContainerService.getImageAnnotationFromContainer(containerInfoUpdate.getFileName());

        if (imageAnnotationDto == null) {
            throw new ImageAnnotationException("ImageAnnotationDto is null. The image might have been deleted from the pending container.");
        }

        return imageAnnotationDto;
    }

    private ImageAnnotationDto saveToApprovedContainer(ImageAnnotationDto updatedContainerInfoDto, ImageAnnotationDto originalContainerInfo,
                                                       Authentication authentication) {
        if (updatedContainerInfoDto == null) {
            return null;
        }

        String image = updatedContainerInfoDto.getData().getImage();
        int imageOccurrencesNotInitial = imageAnnotationService.countByImageAndStatusNotInitial(image);

        saveInitialApprovedImageAnnotation(originalContainerInfo, authentication, imageOccurrencesNotInitial);

        ImageAnnotationDto uploadedImageAnnotationDto =
                approvedContainerService.saveToApprovedContainer(updatedContainerInfoDto, imageOccurrencesNotInitial);

        saveApprovedImageAnnotation(uploadedImageAnnotationDto, updatedContainerInfoDto.isUserApproval(), authentication);
        handleImageOccurrences(uploadedImageAnnotationDto, image, imageOccurrencesNotInitial);
        return uploadedImageAnnotationDto;
    }

    private void handleImageOccurrences(ImageAnnotationDto uploadedImageAnnotationDto, String image, int imageOccurrencesNotInitial) {
        if (uploadedImageAnnotationDto != null && imageOccurrencesNotInitial >= MAX_OCCURRENCES) {
            deleteBlobsForImage(image);
        }
    }

    private void saveInitialApprovedImageAnnotation(ImageAnnotationDto containerInfoDto, Authentication authentication, int imageOccurrencesNotInitial) {
        if (imageOccurrencesNotInitial == 0) {
            imageAnnotationService.saveImageAnnotation(containerInfoDto, authentication);
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

    private ImageAnnotationDto convertToContainerInfo(ImageAnnotationDto imageAnnotationDto,
                                                      ContainerInfoUpdate containerInfoUpdate) {
        if (imageAnnotationDto == null || containerInfoUpdate == null) {
            return null;
        }

        ImageAnnotationDto imageAnnotation = new ImageAnnotationDto();

        imageAnnotation.setClassification(containerInfoUpdate.getClassification());
        imageAnnotation.setRejection(containerInfoUpdate.getRejection());
        imageAnnotation.setComments(containerInfoUpdate.getComments());
        imageAnnotation.setUserApproval(containerInfoUpdate.isUserApproval());
        imageAnnotation.setMesUserDecision(containerInfoUpdate.getMesUserDecision());
        imageAnnotation.setAnnotations(containerInfoUpdate.getAnnotations());
        imageAnnotation.setData(imageAnnotationDto.getData());

        return imageAnnotation;
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
}
