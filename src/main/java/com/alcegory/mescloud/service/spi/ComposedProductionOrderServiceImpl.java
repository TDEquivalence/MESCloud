package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.repository.ComposedProductionOrderRepository;
import com.alcegory.mescloud.model.converter.GenericConverter;
import com.alcegory.mescloud.model.dto.ComposedProductionOrderDto;
import com.alcegory.mescloud.model.dto.ComposedSummaryDto;
import com.alcegory.mescloud.model.dto.ProductionOrderDto;
import com.alcegory.mescloud.model.dto.RequestComposedDto;
import com.alcegory.mescloud.model.entity.ComposedProductionOrderEntity;
import com.alcegory.mescloud.model.entity.ComposedSummaryEntity;
import com.alcegory.mescloud.service.ComposedProductionOrderService;
import com.alcegory.mescloud.service.ProductionOrderService;
import com.alcegory.mescloud.utility.DateUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    private final GenericConverter<ComposedProductionOrderEntity, ComposedProductionOrderDto> converter;

    private final ProductionOrderService productionOrderService;
    private final GenericConverter<ComposedSummaryEntity, ComposedSummaryDto> summaryConverter;

    private static final String OBO_SECTION_PREFIX = "OBO";
    private static final String CODE_PREFIX = "CP";
    private static final int FIRST_CODE_VALUE = 1;
    private static final String FIVE_DIGIT_NUMBER_FORMAT = "%05d";


    @Override
    public Optional<ComposedProductionOrderDto> create(RequestComposedDto requestComposedDto) {
        return create(requestComposedDto.getProductionOrderIds());
    }

    @Override
    public Optional<ComposedProductionOrderDto> create(List<Long> productionOrderIds) {

        List<Long> validProductionOrderIds = getValidProductionOrders(productionOrderIds);
        if (!haveSameProperties(productionOrderIds)) {
            throw new IllegalArgumentException("Production order list doesn't have same properties");
        }
        ComposedProductionOrderEntity composedEntity = createComposed();

        setProductionOrdersWithComposed(validProductionOrderIds, composedEntity);

        saveAndUpdate(composedEntity);
        return Optional.of(converter.toDto(composedEntity, ComposedProductionOrderDto.class));
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
        composedEntity.setCreatedAt(new Date());

        return repository.save(composedEntity);
    }

    private String generateCode() {

        Optional<ComposedProductionOrderEntity> composedProductionOrderOpt = repository.findTopByOrderByIdDesc();
        String codePrefix = OBO_SECTION_PREFIX + CODE_PREFIX + DateUtil.getCurrentYearLastTwoDigits();

        return composedProductionOrderOpt.isEmpty() ?
                codePrefix + FIRST_CODE_VALUE :
                codePrefix + generateFormattedCodeValue(composedProductionOrderOpt.get(), codePrefix);
    }

    private String generateFormattedCodeValue(ComposedProductionOrderEntity composedProductionOrder, String codePrefix) {

        if (composedProductionOrder.getCode() == null || composedProductionOrder.getCode().isEmpty()) {
            String message = "Unable to generate new code: last stored Composed Production Order code is null or empty";
            log.warning(message);
            throw new IllegalStateException(message);
        }

        String lastCodeValueAsString = composedProductionOrder.getCode().substring(codePrefix.length());
        int lastCodeValue = Integer.parseInt(lastCodeValueAsString);

        return String.format(FIVE_DIGIT_NUMBER_FORMAT, ++lastCodeValue);
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
        return converter.toDto(repository.findAll(), ComposedProductionOrderDto.class);
    }

    @Override
    public List<ComposedSummaryDto> findSummarized(boolean withHits) {
        List<ComposedSummaryEntity> composedWithoutHits = repository.getOpenComposedSummaries(withHits);
        return summaryConverter.toDto(composedWithoutHits, ComposedSummaryDto.class);
    }

    @Override
    public List<ComposedSummaryDto> findCompleted() {
        List<ComposedSummaryEntity> composedCompleted = repository.findCompleted();
        return summaryConverter.toDto(composedCompleted, ComposedSummaryDto.class);
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
