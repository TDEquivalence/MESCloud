package com.tde.mescloud.service;

import com.tde.mescloud.model.converter.ComposedProductionOrderConverter;
<<<<<<< HEAD
import com.tde.mescloud.model.dto.ComposedProductionOrderDto;
import com.tde.mescloud.model.dto.RequestComposedDto;
import com.tde.mescloud.model.entity.ComposedProductionOrderEntity;
=======
import com.tde.mescloud.model.converter.ComposedSummaryConverter;
import com.tde.mescloud.model.dto.ComposedProductionOrderDto;
import com.tde.mescloud.model.dto.ComposedSummaryDto;
import com.tde.mescloud.model.dto.RequestComposedDto;
import com.tde.mescloud.model.entity.ComposedProductionOrderEntity;
import com.tde.mescloud.model.entity.ComposedSummaryEntity;
>>>>>>> development
import com.tde.mescloud.repository.ComposedProductionOrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Log
public class ComposedProductionOrderServiceImpl implements ComposedProductionOrderService {

    private final ComposedProductionOrderRepository repository;
    private final ComposedProductionOrderConverter converter;

    private final ProductionOrderService productionOrderService;
<<<<<<< HEAD
=======
    private final ComposedSummaryConverter summaryConverter;
>>>>>>> development

    private static final String CODE_PREFIX = "CP";
    private static final int CODE_INITIAL_VALUE = 0;


    @Override
    public Optional<ComposedProductionOrderDto> create(RequestComposedDto requestComposedDto) {
        return create(requestComposedDto.getProductionOrderIds());
    }

    @Override
    public Optional<ComposedProductionOrderDto> create(List<Long> productionOrderIds) {

        List<Long> validProductionOrderIds = getValidProductionOrders(productionOrderIds);
        ComposedProductionOrderEntity composedEntity = createComposed();

        setProductionOrdersWithComposed(validProductionOrderIds, composedEntity);

        saveAndUpdate(composedEntity);
        return Optional.of(converter.convertToDto(composedEntity));
    }

    private void setProductionOrdersWithComposed(List<Long> validProductionOrderIds, ComposedProductionOrderEntity composedEntity) {
<<<<<<< HEAD
        for(Long id : validProductionOrderIds) {
=======
        for (Long id : validProductionOrderIds) {
>>>>>>> development
            productionOrderService.findById(id).ifPresent(productionOrderEntity -> {
                productionOrderEntity.setComposedProductionOrder(composedEntity);
                composedEntity.getProductionOrders().add(productionOrderEntity);
            });
        }
    }

    private List<Long> getValidProductionOrders(List<Long> productionOrderIds) {
        List<Long> validProductionOrderIds = productionOrderService.findExistingIds(productionOrderIds);
<<<<<<< HEAD
        if(validProductionOrderIds.isEmpty()) {
=======
        if (validProductionOrderIds.isEmpty()) {
>>>>>>> development
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
<<<<<<< HEAD
=======

    @Override
    public List<ComposedSummaryDto> findSummarized(boolean withHits) {
        List<ComposedSummaryEntity> composedWithoutHits = repository.findSummarized(withHits);
        return summaryConverter.toDto(composedWithoutHits);
    }

    @Override
    public List<ComposedSummaryDto> findCompleted() {
        List<ComposedSummaryEntity> composedCompleted = repository.findCompleted();
        return summaryConverter.toDto(composedCompleted);
    }
>>>>>>> development
}
