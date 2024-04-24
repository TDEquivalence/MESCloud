package com.alcegory.mescloud.azure.model.converter;

import com.alcegory.mescloud.azure.model.AnnotationEntity;
import com.alcegory.mescloud.azure.model.ImageAnnotationEntity;
import com.alcegory.mescloud.azure.model.dto.AnnotationDto;
import com.alcegory.mescloud.azure.model.dto.ImageAnnotationDto;
import com.alcegory.mescloud.azure.model.dto.ResultDto;
import com.alcegory.mescloud.azure.model.dto.ValueDto;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Log
@AllArgsConstructor
public class ImageAnnotationConverter {

    public ImageAnnotationEntity dtoToEntity(ImageAnnotationDto imageAnnotationDto) {
        if (imageAnnotationDto == null) {
            return null;
        }

        List<AnnotationDto> annotationDtos = imageAnnotationDto.getAnnotations();

        ImageAnnotationEntity imageAnnotationEntity = imageAnnotationDtoToEntity(imageAnnotationDto);
        if (annotationDtos != null) {
            List<AnnotationEntity> annotationEntities = annotationDtosToEntities(annotationDtos);
            imageAnnotationEntity.setAnnotations(annotationEntities);
        }
        return imageAnnotationEntity;
    }

    private ImageAnnotationEntity imageAnnotationDtoToEntity(ImageAnnotationDto imageAnnotationDto) {
        ImageAnnotationEntity imageAnnotationEntity = new ImageAnnotationEntity();
        imageAnnotationEntity.setImage(imageAnnotationDto.getData() != null ? imageAnnotationDto.getData().getImage() : null);
        imageAnnotationEntity.setModelDecision(imageAnnotationDto.getModelDecision());
        imageAnnotationEntity.setUserDecision(imageAnnotationDto.getUserDecision());
        imageAnnotationEntity.setClassification(imageAnnotationDto.getClassification());
        imageAnnotationEntity.setUserApproval(imageAnnotationDto.isUserApproval());
        imageAnnotationEntity.setRejection(imageAnnotationDto.getRejection());
        imageAnnotationEntity.setComments(imageAnnotationDto.getComments());
        imageAnnotationEntity.setLogDecision(imageAnnotationDto.getLogDecision());
        imageAnnotationEntity.setMesUserDecision(imageAnnotationDto.getMesUserDecision());
        return imageAnnotationEntity;
    }

    private List<AnnotationEntity> annotationDtosToEntities(List<AnnotationDto> annotationDtos) {
        if (annotationDtos == null) {
            return Collections.emptyList();
        }

        List<AnnotationEntity> annotations = new ArrayList<>();
        for (AnnotationDto annotationDto : annotationDtos) {
            for (ResultDto result : annotationDto.getResult()) {
                AnnotationEntity annotationEntity = resultDtoToEntity(result);
                annotations.add(annotationEntity);
            }
        }

        return annotations;
    }

    private AnnotationEntity resultDtoToEntity(ResultDto result) {
        AnnotationEntity annotationEntity = new AnnotationEntity();
        if (result != null) {
            setAnnotationFields(annotationEntity, result);
        }
        return annotationEntity;
    }

    private void setAnnotationFields(AnnotationEntity annotation, ResultDto result) {
        annotation.setFromName(result.getFromName());
        annotation.setToName(result.getToName());
        annotation.setType(result.getType());
        annotation.setOriginalWidth(result.getOriginalWidth());
        annotation.setOriginalHeight(result.getOriginalHeight());
        setValueFields(annotation, result.getValue());
    }

    private void setValueFields(AnnotationEntity annotation, ValueDto value) {
        if (value != null) {
            annotation.setX(value.getX());
            annotation.setY(value.getY());
            annotation.setWidth(value.getWidth());
            annotation.setHeight(value.getHeight());
            annotation.setRotation(value.getRotation());
            annotation.setRectangleLabels(value.getRectangleLabels());
        }
    }
}
