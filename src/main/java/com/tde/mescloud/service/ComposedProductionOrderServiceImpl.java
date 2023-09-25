package com.tde.mescloud.service;

import com.tde.mescloud.model.converter.ComposedProductionOrderConverter;
import com.tde.mescloud.model.converter.ComposedSummaryConverter;
import com.tde.mescloud.model.dto.ComposedProductionOrderDto;
import com.tde.mescloud.model.dto.ComposedSummaryDto;
import com.tde.mescloud.model.dto.ProductionOrderDto;
import com.tde.mescloud.model.dto.RequestComposedDto;
import com.tde.mescloud.model.entity.ComposedProductionOrderEntity;
import com.tde.mescloud.model.entity.ComposedSummaryEntity;
import com.tde.mescloud.repository.ComposedProductionOrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
@Log
public class ComposedProductionOrderServiceImpl implements ComposedProductionOrderService {

    private static final java.util.logging.Logger logger = Logger.getLogger(ComposedProductionOrderServiceImpl.class.getName());

    private final ComposedProductionOrderRepository repository;
    private final ComposedProductionOrderConverter converter;

    private final ProductionOrderService productionOrderService;
    private final ComposedSummaryConverter summaryConverter;

    private static final String CODE_PREFIX = "CP";
    private static final int CODE_INITIAL_VALUE = 0;


    @Override
    public Optional<ComposedProductionOrderDto> create(RequestComposedDto requestComposedDto) {
        return create(requestComposedDto.getProductionOrderIds());
    }

    @Override
    public Optional<ComposedProductionOrderDto> create(List<Long> productionOrderIds) {

        List<Long> validProductionOrderIds = getValidProductionOrders(productionOrderIds);
        if(!haveSameProperties(productionOrderIds)) {
            throw new IllegalArgumentException("Production order list doesn't have same properties");
        }
        ComposedProductionOrderEntity composedEntity = createComposed();

        setProductionOrdersWithComposed(validProductionOrderIds, composedEntity);

        saveAndUpdate(composedEntity);
        return Optional.of(converter.convertToDto(composedEntity));
    }

    private void setProductionOrdersWithComposed(List<Long> validProductionOrderIds, ComposedProductionOrderEntity composedEntity) {
        for (Long id : validProductionOrderIds) {
            productionOrderService.findById(id).ifPresent(productionOrderEntity -> {
                productionOrderEntity.setComposedProductionOrder(composedEntity);
                composedEntity.getProductionOrders().add(productionOrderEntity);
            });
        }
    }

    private List<Long> getValidProductionOrders(List<Long> productionOrderIds) {
        List<Long> validProductionOrderIds = productionOrderService.findExistingIds(productionOrderIds);
        if (validProductionOrderIds.isEmpty()) {
            throw new IllegalArgumentException("Production Order Ids are not valid");
        }

        return validProductionOrderIds;
    }

    private ComposedProductionOrderEntity createComposed() {
        ComposedProductionOrderEntity composedEntity = new ComposedProductionOrderEntity();
        String composedProductionCode = generateCode();
        composedEntity.setCode(composedProductionCode);

        return repository.save(composedEntity);
    }

    private String incrementAndGenerateCode(int lastMaxCode) {
        int codeIncremented = lastMaxCode + 1;
        return CODE_PREFIX + String.format("%05d", codeIncremented);
    }

    private String generateCode() {
        Optional<String> savedLastMaxCode = repository.findLastMaxCode();

        if (savedLastMaxCode.isPresent()) {
            return incrementAndGenerateCode(Integer.parseInt(savedLastMaxCode.get()));
        } else {
            return incrementAndGenerateCode(CODE_INITIAL_VALUE);
        }
    }


    private boolean haveSameProperties(List<Long> productionOrderIds) {
        if (productionOrderIds.isEmpty()) {
            return false;
        }

        List<ProductionOrderDto> productionOrderDtos = getProductionOrderDtos(productionOrderIds);
        ProductionOrderDto firstProductionOrder = productionOrderDtos.get(0);

        return productionOrderDtos.stream().skip(1).allMatch(order ->
                propertiesAreEqual(order, firstProductionOrder)
        );
    }

    private boolean propertiesAreEqual(ProductionOrderDto orderToCompare, ProductionOrderDto order) {
        if (orderToCompare == null || order == null) {
            return false;
        }

        boolean inputBatchEqual = Objects.equals(orderToCompare.getInputBatch(), order.getInputBatch());
        boolean gaugeEqual = Objects.equals(orderToCompare.getGauge(), order.getGauge());
        boolean washingProcessEqual = Objects.equals(orderToCompare.getWashingProcess(), order.getWashingProcess());

        return inputBatchEqual && gaugeEqual && washingProcessEqual;
    }

    private List<ProductionOrderDto> getProductionOrderDtos(List<Long> productionOrderIds) {
        return productionOrderIds.stream()
                .map(productionOrderService::findDtoById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    @Override
    public ComposedProductionOrderEntity saveAndUpdate(ComposedProductionOrderEntity composedEntity) {
        return repository.save(composedEntity);
    }

    @Override
    public void delete(ComposedProductionOrderEntity composedEntity) {
        repository.delete(composedEntity);
    }

    @Override
    public Optional<ComposedProductionOrderEntity> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<ComposedProductionOrderDto> getAll() {
        return converter.convertToDto(repository.findAll());
    }

    @Override
    public List<ComposedSummaryDto> findSummarized(boolean withHits) {
        List<ComposedSummaryEntity> composedWithoutHits = repository.getOpenComposedSummaries(withHits);
        return summaryConverter.toDto(composedWithoutHits);
    }

    @Override
    public List<ComposedSummaryDto> findCompleted() {
        List<ComposedSummaryEntity> composedCompleted = repository.findCompleted();
        return summaryConverter.toDto(composedCompleted);
    }

    @Override
    public void setProductionOrderApproval(ComposedProductionOrderEntity composed, boolean isApproved) {
        try {
            if (composed == null) {
                throw new IllegalArgumentException("ComposedProductionOrderEntity is null");
            }

            Long composedId = composed.getId();

            Optional<ComposedProductionOrderEntity> composedEntityOptional = repository.findById(composedId);
            if (composedEntityOptional.isEmpty()) {
                throw new EntityNotFoundException("ComposedProductionOrderEntity not found with ID: " + composedId);
            }

            productionOrderService.setProductionOrderApproval(composedId, isApproved);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            logger.warning("Error in setProductionOrderApproval: " + e.getMessage());
        }
    }
}
