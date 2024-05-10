package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.model.converter.CountingEquipmentConverter;
import com.alcegory.mescloud.model.converter.ProductionOrderConverter;
import com.alcegory.mescloud.model.dto.CountingEquipmentDto;
import com.alcegory.mescloud.model.dto.CountingEquipmentInfoDto;
import com.alcegory.mescloud.model.dto.ProductionOrderDto;
import com.alcegory.mescloud.model.entity.CountingEquipmentEntity;
import com.alcegory.mescloud.model.entity.ProductionOrderEntity;
import com.alcegory.mescloud.repository.ProductionOrderRepository;
import com.alcegory.mescloud.service.CountingEquipmentInfoService;
import com.alcegory.mescloud.service.CountingEquipmentService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@Log
@AllArgsConstructor
public class CountingEquipmentInfoServiceImpl implements CountingEquipmentInfoService {
    
    private final CountingEquipmentService countingEquipmentService;
    private final ProductionOrderRepository productionOrderRepository;

    private final CountingEquipmentConverter converter;
    private final ProductionOrderConverter productionOrderConverter;

    public Optional<CountingEquipmentInfoDto> findEquipmentWithProductionOrderById(long id) {
        CountingEquipmentDto countingEquipmentOpt = findEquipmentById(id);
        ProductionOrderDto productionOrderDto = findProductionOrderByEquipmentId(id);
        CountingEquipmentInfoDto infoDto = new CountingEquipmentInfoDto();
        infoDto.setCountingEquipment(countingEquipmentOpt);
        infoDto.setProductionOrder(productionOrderDto);

        return Optional.of(infoDto);
    }

    public CountingEquipmentDto findEquipmentById(long id) {
        Optional<CountingEquipmentEntity> countingEquipmentOpt = countingEquipmentService.findByIdWithLastProductionOrder(id);
        if (countingEquipmentOpt.isEmpty()) {
            log.warning(() -> String.format("COUNTING_EQUIPMENT_ID_NOT_FOUND", id));
            return null;
        }

        CountingEquipmentEntity countingEquipment = countingEquipmentOpt.get();
        if (countingEquipment.getOutputs().isEmpty()) {
            log.warning(() -> String.format("COUNTING_EQUIPMENT_ID_NOT_FOUND", id));
            return null;
        }

        return convertToDtoWithActiveProductionOrder(countingEquipment);
    }

    private CountingEquipmentDto convertToDtoWithActiveProductionOrder(CountingEquipmentEntity entity) {

        CountingEquipmentDto dto = converter.convertToDto(entity);

        if (hasSingleActiveProductionOrder(entity)) {
            dto.setProductionOrderCode(entity.getProductionOrders().get(0).getCode());
        }

        return dto;
    }

    private boolean hasSingleActiveProductionOrder(CountingEquipmentEntity entity) {

        return entity.getProductionOrders() != null &&
                entity.getProductionOrders().size() == 1 &&
                !entity.getProductionOrders().get(0).isCompleted();
    }

    public ProductionOrderDto findProductionOrderByEquipmentId(long equipmentId) {
        Optional<ProductionOrderEntity> productionOrderOpt = productionOrderRepository.findLastByEquipmentId(equipmentId);
        if (productionOrderOpt.isEmpty() || productionOrderOpt.get().isCompleted()) {
            return null;
        }

        ProductionOrderEntity productionOrder = productionOrderOpt.get();
        return productionOrderConverter.toDto(productionOrder);
    }
}
