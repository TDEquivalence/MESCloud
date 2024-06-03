package com.alcegory.mescloud.model.converter;

import com.alcegory.mescloud.model.dto.composed.ComposedExportInfoDto;
import com.alcegory.mescloud.model.dto.composed.ExportComposedDto;
import com.alcegory.mescloud.model.dto.production.ProductionInstructionDto;
import com.alcegory.mescloud.model.entity.composed.ComposedSummaryEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ComposedConverterImpl implements ComposedConverter {

    public List<ComposedExportInfoDto> convertToDtoList(List<ComposedSummaryEntity> entities) {
        List<ComposedExportInfoDto> dtoList = new ArrayList<>();

        for (ComposedSummaryEntity entity : entities) {
            ComposedExportInfoDto dto = convertToDto(entity);
            dtoList.add(dto);
        }

        return dtoList;
    }

    public ComposedExportInfoDto convertToDto(ComposedSummaryEntity entity) {
        ComposedExportInfoDto dto = new ComposedExportInfoDto();

        dto.setComposedCode(entity.getCode());
        dto.setComposedCreatedAt(entity.getCreatedAt());
        dto.setApprovedAt(entity.getApprovedAt());
        dto.setHitInsertedAt(entity.getHitInsertedAt());
        dto.setSampleAmount(entity.getSampleAmount());
        dto.setReliability(entity.getReliability());
        dto.setIsBatchApproved(entity.getIsBatchApproved());
        dto.setBatchCode(entity.getBatchCode());
        dto.setAmountOfHits(entity.getAmountOfHits());
        dto.setValidAmount(entity.getValidAmount());

        List<ProductionInstructionDto> instructionDtos = new ArrayList<>();
        if (entity.getInstructions() != null) {
            for (Map<String, Object> instructionMap : entity.getInstructions()) {
                ProductionInstructionDto instructionDto = new ProductionInstructionDto();
                instructionDto.setName((String) instructionMap.get("name"));
                instructionDto.setValue((String) instructionMap.get("value"));
                instructionDtos.add(instructionDto);
            }
        }
        dto.setInstructions(instructionDtos);

        return dto;
    }

    public List<ExportComposedDto> convertToExportDtoList(List<ComposedSummaryEntity> entities) {
        List<ExportComposedDto> dtoList = new ArrayList<>();

        for (ComposedSummaryEntity entity : entities) {
            ExportComposedDto dto = convertToExportDto(entity);
            dtoList.add(dto);
        }

        return dtoList;
    }

    public ExportComposedDto convertToExportDto(ComposedSummaryEntity entity) {
        ExportComposedDto dto = new ExportComposedDto();
        List<ProductionInstructionDto> instructionDtos = new ArrayList<>();

        Field[] fields = ComposedSummaryEntity.class.getDeclaredFields();
        for (Field field : fields) {
            boolean originalAccessibility = field.canAccess(entity);

            try {
                Object value = field.get(entity);
                addInstruction(instructionDtos, field.getName(), value);
            } catch (IllegalAccessException e) {
                log.error("Error accessing field: {}", e.getMessage());
            } finally {
                field.setAccessible(originalAccessibility);
            }
        }

        dto.setInstructions(instructionDtos);
        return dto;
    }

    private void addInstruction(List<ProductionInstructionDto> instructionDtos, String name, Object value) {
        ProductionInstructionDto instructionDto = new ProductionInstructionDto();
        instructionDto.setName(name);
        instructionDto.setValue(value != null ? value.toString() : null);
        instructionDtos.add(instructionDto);
    }

}
