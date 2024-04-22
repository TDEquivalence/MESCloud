package com.alcegory.mescloud.azure.service;

import com.alcegory.mescloud.azure.model.AnnotationEntity;
import com.alcegory.mescloud.azure.model.ImageAnnotationEntity;
import com.alcegory.mescloud.azure.model.converter.ImageAnnotationConverter;
import com.alcegory.mescloud.azure.model.dto.ImageAnnotationDto;
import com.alcegory.mescloud.azure.repository.AnnotationRepository;
import com.alcegory.mescloud.azure.repository.ImageAnnotationRepository;
import com.alcegory.mescloud.exception.ImageAnnotationSaveException;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.alcegory.mescloud.azure.model.constant.Status.RECEIVED;

@Service
@AllArgsConstructor
@Log
public class ImageAnnotationServiceImpl implements ImageAnnotationService {

    private final ImageAnnotationRepository imageAnnotationRepository;
    private final AnnotationRepository annotationRepository;
    private final ImageAnnotationConverter converter;

    @Override
    public void saveImageAnnotation(ImageAnnotationDto imageAnnotationDto) {
        if (imageAnnotationDto == null) {
            throw new IllegalArgumentException("ImageAnnotationDto cannot be null");
        }

        ImageAnnotationEntity imageAnnotation = converter.dtoToEntity(imageAnnotationDto);
        imageAnnotation.setRegisteredAt(new Date());
        imageAnnotation.setStatus(RECEIVED);
        saveImageWithTransaction(imageAnnotation);
    }

    public void saveImageWithTransaction(ImageAnnotationEntity imageAnnotation) {
        if (imageAnnotation == null) {
            throw new IllegalArgumentException("ImageAnnotationEntity cannot be null");
        }

        try {
            ImageAnnotationEntity persistedImageAnnotation = imageAnnotationRepository.save(imageAnnotation);

            List<AnnotationEntity> annotationEntities = persistedImageAnnotation.getAnnotations();
            if (annotationEntities != null && !annotationEntities.isEmpty()) {
                for (AnnotationEntity annotation : annotationEntities) {
                    annotation.setImageAnnotation(persistedImageAnnotation);
                }
                annotationRepository.saveAll(annotationEntities);
            }
        } catch (Exception e) {
            throw new ImageAnnotationSaveException("Failed to save image annotation", e);
        }
    }
}
