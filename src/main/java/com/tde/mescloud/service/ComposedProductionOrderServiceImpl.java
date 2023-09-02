package com.tde.mescloud.service;

import com.tde.mescloud.model.converter.ComposedProductionOrderConverter;
import com.tde.mescloud.model.dto.ComposedProductionOrderDto;
import com.tde.mescloud.model.dto.ProductionOrderDto;
import com.tde.mescloud.model.dto.RequestSampleDto;
import com.tde.mescloud.model.entity.ComposedProductionOrderEntity;
import com.tde.mescloud.model.entity.ProductionOrderEntity;
import com.tde.mescloud.repository.ComposedProductionOrderRepository;
import com.tde.mescloud.repository.ProductionOrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Log
public class ComposedProductionOrderServiceImpl implements ComposedProductionOrderService {

    private final ComposedProductionOrderRepository composedProductionOrderRepository;
    private final ComposedProductionOrderConverter composedConverter;

    private final ProductionOrderRepository productionOrderRepository;

    private static final String CODE_PREFIX = "CP";
    private static final int CODE_INITIAL_VALUE = 0;

    @Override
    public Optional<ComposedProductionOrderDto> create(ProductionOrderDto[] requestComposedArticleDto) {
        ComposedProductionOrderEntity composedEntity = createComposed();

        for(ProductionOrderDto po : requestComposedArticleDto) {
            Optional<ProductionOrderEntity> productionOrderEntity = productionOrderRepository.findById(po.getId());
            if(productionOrderEntity.isEmpty()) {
                continue;
            }
            productionOrderEntity.get().setComposedProductionOrder(composedEntity);
            composedEntity.getProductionOrderEntity().add(productionOrderEntity.get());
            productionOrderRepository.save(productionOrderEntity.get());
        }

        composedProductionOrderRepository.save(composedEntity);
        if(composedEntity.getProductionOrderEntity() == null) {
            composedProductionOrderRepository.delete(composedEntity);
            return Optional.empty();
        }

        return Optional.of(composedConverter.convertToDto(composedEntity));
    }

    @Override
    public Optional<ComposedProductionOrderDto> createSample(RequestSampleDto requestSampleDto) {
        ComposedProductionOrderEntity composedEntity = createComposed();

        return Optional.empty();
    }

    private ComposedProductionOrderEntity createComposed() {
        ComposedProductionOrderDto composedDto = new ComposedProductionOrderDto();
        String composedProductionCode = generateCode();
        composedDto.setCode(composedProductionCode);

        ComposedProductionOrderEntity composedEntity = composedConverter.convertToEntity(composedDto);
        return composedProductionOrderRepository.save(composedEntity);
    }

    private String incrementAndGenerateCode(int lastMaxCode) {
        int codeIncremented = lastMaxCode + 1;
        return CODE_PREFIX + String.format("%05d", codeIncremented);
    }

    private String generateCode() {
        Optional<String> savedLastMaxCode = composedProductionOrderRepository.findLastMaxCode();

        if (savedLastMaxCode.isPresent()) {
            return incrementAndGenerateCode(Integer.parseInt(savedLastMaxCode.get()));
        } else {
            return incrementAndGenerateCode(CODE_INITIAL_VALUE);
        }
    }

}
