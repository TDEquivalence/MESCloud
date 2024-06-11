package com.alcegory.mescloud.service.composed;

import com.alcegory.mescloud.exception.InconsistentPropertiesException;
import com.alcegory.mescloud.model.converter.GenericConverter;
import com.alcegory.mescloud.model.dto.composed.ComposedProductionOrderDto;
import com.alcegory.mescloud.model.dto.composed.ComposedSummaryDto;
import com.alcegory.mescloud.model.dto.pagination.PaginatedComposedDto;
import com.alcegory.mescloud.model.dto.production.ProductionInstructionDto;
import com.alcegory.mescloud.model.dto.production.ProductionOrderDto;
import com.alcegory.mescloud.model.entity.composed.ComposedProductionOrderEntity;
import com.alcegory.mescloud.model.entity.composed.ComposedSummaryEntity;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.model.request.RequestComposedDto;
import com.alcegory.mescloud.repository.composed.ComposedProductionOrderRepository;
import com.alcegory.mescloud.service.production.ProductionOrderService;
import com.alcegory.mescloud.utility.DateUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
@Log
public class ComposedProductionOrderServiceImpl implements ComposedProductionOrderService {

    private static final java.util.logging.Logger logger = Logger.getLogger(ComposedProductionOrderServiceImpl.class.getName());
    private static final String OBO_SECTION_PREFIX = "OBO";
    private static final String CODE_PREFIX = "CP";
    private static final int FIRST_CODE_VALUE = 1;
    private static final String FIVE_DIGIT_NUMBER_FORMAT = "%05d";
    private final ComposedProductionOrderRepository repository;
    private final ProductionOrderService productionOrderService;
    private final GenericConverter<ComposedProductionOrderEntity, ComposedProductionOrderDto> converter;
    private final GenericConverter<ComposedSummaryEntity, ComposedSummaryDto> summaryConverter;

    @Override
    public Optional<ComposedProductionOrderDto> create(RequestComposedDto requestComposedDto) {
        return create(requestComposedDto.getProductionOrderIds());
    }

    @Override
    public Optional<ComposedProductionOrderDto> create(List<Long> productionOrderIds) {

        List<Long> validProductionOrderIds = getValidProductionOrders(productionOrderIds);
        validateProductionOrderProperties(productionOrderIds);

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
                codePrefix + String.format(FIVE_DIGIT_NUMBER_FORMAT, FIRST_CODE_VALUE) :
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

    private void validateProductionOrderProperties(List<Long> productionOrderIds) {
        if (productionOrderIds.isEmpty()) {
            throw new IllegalArgumentException("Production order IDs list cannot be empty");
        }

        List<ProductionOrderDto> productionOrderDtos = getProductionOrderDtos(productionOrderIds);
        ProductionOrderDto firstProductionOrder = productionOrderDtos.get(0);

        boolean allPropertiesEqual = productionOrderDtos.stream()
                .skip(1)
                .allMatch(order -> propertiesAreEqual(order, firstProductionOrder));

        if (!allPropertiesEqual) {
            throw new InconsistentPropertiesException("Production orders do not have the same properties");
        }
    }

    private boolean propertiesAreEqual(ProductionOrderDto orderToCompare, ProductionOrderDto order) {
        if (orderToCompare == null || order == null) {
            return false;
        }

        List<ProductionInstructionDto> instructionsToCompare = orderToCompare.getInstructions();
        List<ProductionInstructionDto> instructions = order.getInstructions();

        if (instructionsToCompare.size() != instructions.size()) {
            return false;
        }

        Map<String, String> instructionMapToCompare = new HashMap<>();
        for (ProductionInstructionDto instructionToCompare : instructionsToCompare) {
            instructionMapToCompare.put(instructionToCompare.getName(), instructionToCompare.getValue());
        }

        for (ProductionInstructionDto instruction : instructions) {
            String valueToCompare = instructionMapToCompare.get(instruction.getName());
            if (valueToCompare == null || !valueToCompare.equals(instruction.getValue())) {
                return false;
            }
        }

        return true;
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
    public PaginatedComposedDto findAllSummarized(long sectionId, boolean withHits) {
        return getOpenComposedSummaries(sectionId, withHits, null);
    }

    @Override
    public PaginatedComposedDto findSummarizedFiltered(long sectionId, boolean withHits, Filter filter) {
        return getOpenComposedSummaries(sectionId, withHits, filter);
    }

    private PaginatedComposedDto getOpenComposedSummaries(long sectionId, boolean withHits, Filter filter) {
        int requestedComposed = filter.getTake();
        filter.setTake(filter.getTake() + 1);

        Long composedId = getComposedIdFromFilter(filter);
        List<ComposedSummaryEntity> composedWithoutHits = repository.getOpenComposedSummaries(sectionId, withHits,
                filter, composedId);

        boolean hasNextPage = composedWithoutHits.size() > requestedComposed;

        if (hasNextPage) {
            composedWithoutHits.remove(composedWithoutHits.size() - 1);
        }

        List<ComposedSummaryDto> composedSummaryDtos = summaryConverter.toDto(composedWithoutHits, ComposedSummaryDto.class);
        PaginatedComposedDto paginatedComposedDto = new PaginatedComposedDto();
        paginatedComposedDto.setHasNextPage(hasNextPage);
        paginatedComposedDto.setComposedProductionOrders(composedSummaryDtos);

        return paginatedComposedDto;
    }

    private Long getComposedIdFromFilter(Filter filter) {
        if (filter != null && filter.getSearch() != null && filter.getSearch().getValue(Filter.Property.PRODUCTION_ORDER_CODE) != null) {
            String productionOrderCode = filter.getSearch().getValue(Filter.Property.PRODUCTION_ORDER_CODE).trim();
            if (!productionOrderCode.isEmpty()) {
                return productionOrderService.findComposedProductionOrderIdByCode(productionOrderCode);
            }
        }
        return null;
    }

    @Override
    public List<ComposedSummaryDto> findAllCompleted(long sectionId) {
        List<ComposedSummaryEntity> composedCompleted = repository.findCompleted(sectionId, null, null);
        return summaryConverter.toDto(composedCompleted, ComposedSummaryDto.class);
    }

    @Override
    public PaginatedComposedDto findCompletedFiltered(long sectionId, Filter filter) {
        int requestedComposed = filter.getTake();
        filter.setTake(filter.getTake() + 1);
        Long composedId = getComposedIdFromFilter(filter);

        List<ComposedSummaryEntity> composedCompleted = repository.findCompleted(sectionId, filter, composedId);

        boolean hasNextPage = composedCompleted.size() > requestedComposed;

        if (hasNextPage) {
            composedCompleted.remove(composedCompleted.size() - 1);
        }

        List<ComposedSummaryDto> summaryDtos = summaryConverter.toDto(composedCompleted, ComposedSummaryDto.class);
        PaginatedComposedDto paginatedComposedDto = new PaginatedComposedDto();
        paginatedComposedDto.setHasNextPage(hasNextPage);
        paginatedComposedDto.setComposedProductionOrders(summaryDtos);

        return paginatedComposedDto;
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
            composed.setApprovedAt(new Date());
            repository.save(composed);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            logger.warning("Error in setProductionOrderApproval: " + e.getMessage());
        }
    }

    @Override
    public void setHitInsertAtInComposed(ComposedProductionOrderEntity composed) {
        if (composed == null) {
            throw new EntityNotFoundException("ComposedProductionOrderEntity not found");
        }
        composed.setHitInsertedAt(new Date());
        repository.save(composed);
    }

    @Override
    public List<ProductionOrderDto> getProductionOrderSummaryByComposedId(Long composedId) {
        if (composedId == null) {
            throw new IllegalArgumentException("Cannot get PO's, because composed id is null");
        }
        return productionOrderService.getProductionOrderByComposedId(composedId);
    }

    @Override
    public void deleteComposed(ComposedProductionOrderEntity composedProductionOrder) {
        repository.delete(composedProductionOrder);
    }

}