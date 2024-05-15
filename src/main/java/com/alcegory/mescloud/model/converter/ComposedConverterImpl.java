package com.alcegory.mescloud.model.converter;

import com.alcegory.mescloud.model.dto.composed.ComposedInfoDto;
import com.alcegory.mescloud.model.dto.composed.ExportComposedDto;
import com.alcegory.mescloud.model.dto.production.ProductionInstructionDto;
import com.alcegory.mescloud.model.entity.composed.ComposedSummaryEntity;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Log
public class ComposedConverterImpl implements ComposedConverter {

    public List<ComposedInfoDto> convertToDtoList(List<ComposedSummaryEntity> entities) {
        List<ComposedInfoDto> dtoList = new ArrayList<>();

        for (ComposedSummaryEntity entity : entities) {
            ComposedInfoDto dto = convertToDto(entity);
            dtoList.add(dto);
        }

        return dtoList;
    }

    public ComposedInfoDto convertToDto(ComposedSummaryEntity entity) {
        ComposedInfoDto dto = new ComposedInfoDto();

        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setCreatedAt(entity.getCreatedAt());
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
            field.setAccessible(true);

            try {
                Object value = field.get(entity);
                addInstruction(instructionDtos, field.getName(), value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
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
