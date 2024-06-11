package com.alcegory.mescloud.service.management;

import com.alcegory.mescloud.model.converter.CountingEquipmentConverter;
import com.alcegory.mescloud.model.converter.ProductionOrderConverter;
import com.alcegory.mescloud.model.dto.equipment.CountingEquipmentDto;
import com.alcegory.mescloud.model.dto.equipment.CountingEquipmentInfoDto;
import com.alcegory.mescloud.model.dto.pagination.PaginatedProductionOrderDto;
import com.alcegory.mescloud.model.dto.production.ProductionOrderDto;
import com.alcegory.mescloud.model.dto.production.ProductionOrderInfoDto;
import com.alcegory.mescloud.model.entity.equipment.CountingEquipmentEntity;
import com.alcegory.mescloud.model.entity.production.ProductionOrderEntity;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.service.equipment.CountingEquipmentService;
import com.alcegory.mescloud.service.production.ProductionOrderService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.alcegory.mescloud.model.filter.Filter.Property.END_DATE;
import static com.alcegory.mescloud.model.filter.Filter.Property.START_DATE;

@Service
@Log
@AllArgsConstructor
public class ManagementInfoServiceImpl implements ManagementInfoService {

    private final CountingEquipmentService countingEquipmentService;
    private final ProductionOrderService productionOrderService;
    private final CountingEquipmentConverter countingEquipmentConverter;
    private final ProductionOrderConverter productionOrderConverter;
    
    public Optional<CountingEquipmentInfoDto> findEquipmentWithProductionOrderById(long id) {
        CountingEquipmentDto countingEquipmentOpt = findEquipmentById(id);
        ProductionOrderInfoDto productionOrderDto = findProductionOrderByEquipmentId(id);
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
        CountingEquipmentDto dto = countingEquipmentConverter.convertToDto(entity);

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

    public ProductionOrderInfoDto findProductionOrderByEquipmentId(long equipmentId) {
        Optional<ProductionOrderEntity> productionOrderOpt = productionOrderService.findActiveByEquipmentId(equipmentId);
        if (productionOrderOpt.isEmpty() || productionOrderOpt.get().isCompleted()) {
            return null;
        }

        ProductionOrderEntity productionOrder = productionOrderOpt.get();
        return productionOrderConverter.toInfoDto(productionOrder);
    }

    @Override
    public PaginatedProductionOrderDto getCompletedWithoutComposedFiltered(long sectionId, Filter filter) {
        int requestedProductionOrders = filter.getTake();
        filter.setTake(filter.getTake() + 1);

        List<ProductionOrderEntity> persistedProductionOrders = productionOrderService.findCompleted(sectionId, true,
                filter, filter.getSearch().getTimestampValue(START_DATE), filter.getSearch().getTimestampValue(END_DATE));
        boolean hasNextPage = persistedProductionOrders.size() > requestedProductionOrders;

        if (hasNextPage) {
            persistedProductionOrders.remove(persistedProductionOrders.size() - 1);
        }

        PaginatedProductionOrderDto paginatedProductionOrderDto = new PaginatedProductionOrderDto();
        paginatedProductionOrderDto.setHasNextPage(hasNextPage);

        List<ProductionOrderDto> summaryDtos = productionOrderConverter.toDto(persistedProductionOrders);

        paginatedProductionOrderDto.setProductionOrders(summaryDtos);
        return paginatedProductionOrderDto;
    }
}