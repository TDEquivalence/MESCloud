package com.alcegory.mescloud.model.converter;

import com.alcegory.mescloud.constant.MqttDTOConstants;
import com.alcegory.mescloud.model.dto.ImsDto;
import com.alcegory.mescloud.model.dto.production.*;
import com.alcegory.mescloud.model.entity.ImsEntity;
import com.alcegory.mescloud.model.entity.production.ProductionInstructionEntity;
import com.alcegory.mescloud.model.entity.production.ProductionOrderEntity;
import com.alcegory.mescloud.model.request.RequestProductionOrderDto;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
@Log
@AllArgsConstructor
public class ProductionOrderConverterImpl implements ProductionOrderConverter {

    private final GenericConverter<ImsEntity, ImsDto> imsConverter;
    private final CountingEquipmentConverter countingEquipmentConverter;

    @Override
    public ProductionOrderEntity toEntity(ProductionOrderDto productionOrderDto) {
        return toEntityInternal(productionOrderDto.getId(),
                productionOrderDto.getCode(),
                productionOrderDto.getTargetAmount(),
                productionOrderDto.getCreatedAt(),
                productionOrderDto.getCompletedAt(),
                productionOrderDto.getInstructions());
    }

    @Override
    public ProductionOrderEntity toEntity(RequestProductionOrderDto requestProductionOrderDto) {
        return toEntityInternal(requestProductionOrderDto.getId(),
                requestProductionOrderDto.getImsCode(),
                requestProductionOrderDto.getTargetAmount(),
                requestProductionOrderDto.getCreatedAt(),
                requestProductionOrderDto.getCompletedAt(),
                requestProductionOrderDto.getInstructions());
    }

    private ProductionOrderEntity toEntityInternal(long id, String code, int targetAmount, Date createdAt, Date completedAt,
                                                   List<ProductionInstructionDto> instructions) {
        ProductionOrderEntity productionOrderEntity = new ProductionOrderEntity();
        productionOrderEntity.setId(id);
        productionOrderEntity.setCode(code);
        productionOrderEntity.setTargetAmount(targetAmount);
        productionOrderEntity.setCreatedAt(createdAt);
        productionOrderEntity.setCompletedAt(completedAt);
        productionOrderEntity.setProductionInstructions(toEntityList(instructions));
        return productionOrderEntity;
    }

    @Override
    public ProductionOrderMqttDto toMqttDto(ProductionOrderEntity entity, boolean isEquipmentEnabled) {

        ProductionOrderMqttDto mqttDto = new ProductionOrderMqttDto();
        mqttDto.setJsonType(MqttDTOConstants.PRODUCTION_ORDER_DTO_NAME);
        mqttDto.setProductionOrderCode(entity.getCode());
        mqttDto.setTargetAmount(entity.getTargetAmount());
        mqttDto.setEquipmentEnabled(isEquipmentEnabled);

        if (entity.getEquipment() != null) {
            mqttDto.setEquipmentCode(entity.getEquipment().getCode());
        }

        return mqttDto;
    }

    @Override
    public ProductionOrderDto toDto(ProductionOrderEntity entity) {
        ProductionOrderDto dto = new ProductionOrderDto();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setTargetAmount(entity.getTargetAmount());
        dto.setValidAmount(entity.getValidAmount());
        dto.setIsCompleted(entity.isCompleted());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setCompletedAt(entity.getCompletedAt());
        dto.setComposedCode(entity.getComposedProductionOrder() != null ?
                entity.getComposedProductionOrder().getCode() : null);

        Optional<ImsDto> imsDtoOptional = Optional.ofNullable(entity.getIms())
                .map(ims -> imsConverter.toDto(ims, ImsDto.class));
        dto.setIms(imsDtoOptional.orElse(null));

        dto.setEquipment(Optional.ofNullable(entity.getEquipment())
                .map(countingEquipmentConverter::convertToDto)
                .orElse(null));

        dto.setInstructions(toDtoList(entity.getProductionInstructions()));
        return dto;
    }

    public ProductionOrderInfoDto toInfoDto(ProductionOrderEntity entity) {
        ProductionOrderInfoDto infoDto = new ProductionOrderInfoDto();
        infoDto.setId(entity.getId());
        infoDto.setCode(entity.getCode());
        infoDto.setValidAmount(entity.getValidAmount());
        infoDto.setIsCompleted(entity.isCompleted());
        infoDto.setCreatedAt(entity.getCreatedAt());
        infoDto.setCompletedAt(entity.getCompletedAt());

        infoDto.setInstructions(toDtoList(entity.getProductionInstructions()));
        return infoDto;
    }

    @Override
    public List<ProductionInstructionDto> toDtoList(List<ProductionInstructionEntity> entities) {
        List<ProductionInstructionDto> dtos = new ArrayList<>();
        if (entities != null) {
            for (ProductionInstructionEntity entity : entities) {
                ProductionInstructionDto dto = toDto(entity);
                dtos.add(dto);
            }
        }
        return dtos;
    }

    @Override
    public List<ExportProductionOrderDto> toExportDtoList(List<ProductionOrderEntity> entities) {
        List<ExportProductionOrderDto> exportDtoList = new ArrayList<>();

        for (ProductionOrderEntity entity : entities) {
            ExportProductionOrderDto exportDto = toExportDtoList(entity);
            exportDtoList.add(exportDto);
        }

        return exportDtoList;
    }

    public ExportProductionOrderDto toExportDtoList(ProductionOrderEntity entity) {
        ExportProductionOrderDto exportDto = new ExportProductionOrderDto();

        exportDto.setEquipment(entity.getEquipment().getCode());
        exportDto.setComposedCode(entity.getComposedProductionOrder().getCode());
        exportDto.setCode(entity.getCode());
        exportDto.setIms(entity.getIms().getCode());
        exportDto.setInstructionDtos(toDtoList(entity.getProductionInstructions()));
        exportDto.setValidAmount(entity.getValidAmount());
        exportDto.setCreatedAt(entity.getCreatedAt());
        exportDto.setCompletedAt(entity.getCompletedAt());
        return exportDto;
    }

    public ProductionInstructionDto toDto(ProductionInstructionEntity entity) {
        ProductionInstructionDto dto = new ProductionInstructionDto();
        if (entity != null) {
            dto.setName(entity.getName());
            dto.setValue(entity.getValue());
        }
        return dto;
    }

    public List<ProductionInstructionEntity> toEntityList(List<ProductionInstructionDto> dtos) {
        List<ProductionInstructionEntity> entities = new ArrayList<>();
        if (dtos != null) {
            for (ProductionInstructionDto dto : dtos) {
                ProductionInstructionEntity entity = toEntity(dto);
                entities.add(entity);
            }
        }
        return entities;
    }

    public ProductionInstructionEntity toEntity(ProductionInstructionDto dto) {
        ProductionInstructionEntity entity = new ProductionInstructionEntity();
        if (dto != null) {
            entity.setName(dto.getName());
            entity.setValue(dto.getValue());
        }
        return entity;
    }
}
