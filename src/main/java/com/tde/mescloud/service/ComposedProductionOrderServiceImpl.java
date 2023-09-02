package com.tde.mescloud.service;

import com.tde.mescloud.model.converter.ComposedProductionOrderConverter;
import com.tde.mescloud.model.dto.ComposedProductionOrderDto;
import com.tde.mescloud.model.dto.RequestComposedDto;
import com.tde.mescloud.model.entity.ComposedProductionOrderEntity;
import com.tde.mescloud.model.entity.ProductionOrderEntity;
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

    private static final String CODE_PREFIX = "CP";
    private static final int CODE_INITIAL_VALUE = 0;
    @Override
    public Optional<ComposedProductionOrderDto> create(RequestComposedDto productionOrderIds) {

        List<Long> validProductionOrderIds = productionOrderService.checkOrderIdsExistInDatabase(productionOrderIds.getProductionOrderIds());
        if(validProductionOrderIds.isEmpty()) {
            return Optional.empty();
        }

        ComposedProductionOrderEntity composedEntity = createComposed();

        for(Long id : validProductionOrderIds) {
            Optional<ProductionOrderEntity> productionOrderEntity = productionOrderService.findById(id);
            if(productionOrderEntity.isEmpty()) {
                continue;
            }
            productionOrderEntity.get().setComposedProductionOrder(composedEntity);
            composedEntity.getProductionOrders().add(productionOrderEntity.get());
            productionOrderService.saveAndUpdate(productionOrderEntity.get());
        }

        saveAndUpdate(composedEntity);
        return Optional.of(converter.convertToDto(composedEntity));
    }

    @Override
    public Optional<ComposedProductionOrderDto> create(List<Long> productionOrderIds) {
        RequestComposedDto requestComposedDto = new RequestComposedDto();
        requestComposedDto.setProductionOrderIds(productionOrderIds);
        return create(requestComposedDto);
    }

    private ComposedProductionOrderEntity createComposed() {
        ComposedProductionOrderDto composedDto = new ComposedProductionOrderDto();
        String composedProductionCode = generateCode();
        composedDto.setCode(composedProductionCode);

        ComposedProductionOrderEntity composedEntity = converter.convertToEntity(composedDto);
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
}
