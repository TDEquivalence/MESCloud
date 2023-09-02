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
    private final ComposedProductionOrderConverter composedArticleConverter;

    private final ProductionOrderRepository productionOrderRepository;

    private static final String COMPOSED_PRODUCTION_CODE_PREFIX = "CP";
    private static final int COMPOSED_PRODUCTION_CODE_INIT = 0;

    @Override
    public Optional<ComposedProductionOrderDto> create(ProductionOrderDto[] requestComposedArticleDto) {
        ComposedProductionOrderDto composedProductionOrderDto = new ComposedProductionOrderDto();
        String composedProductionCode = generateComposedArticleCode();
        composedProductionOrderDto.setCode(composedProductionCode);

        ComposedProductionOrderEntity composedProductionOrderEntity = composedArticleConverter.convertToEntity(composedProductionOrderDto);

        for(ProductionOrderDto po : requestComposedArticleDto) {
            Optional<ProductionOrderEntity> productionOrderEntity = productionOrderRepository.findById(po.getId());
            if(productionOrderEntity.isEmpty()) {
                continue;
            }
            productionOrderEntity.get().setComposedProductionOrder(composedProductionOrderEntity);
            composedProductionOrderEntity.getProductionOrderEntity().add(productionOrderEntity.get());
            composedProductionOrderRepository.save(composedProductionOrderEntity);
        }

        if(composedProductionOrderEntity.getProductionOrderEntity() == null) {
            composedProductionOrderRepository.delete(composedProductionOrderEntity);
            return Optional.empty();
        }

        return Optional.of(composedArticleConverter.convertToDto(composedProductionOrderEntity));
    }

    @Override
    public Optional<ComposedProductionOrderDto> createSample(RequestSampleDto requestSampleDto) {
        return Optional.empty();
    }

    private String generateComposedArticleCode(int lastMaxCode) {
        int codeIncremented = lastMaxCode + 1;
        return COMPOSED_PRODUCTION_CODE_PREFIX + String.format("%05d", codeIncremented);
    }

    private String generateComposedArticleCode() {
        Optional<String> savedLastMaxCode = composedProductionOrderRepository.findLastMaxCode();

        if (savedLastMaxCode.isPresent()) {
            return generateComposedArticleCode(Integer.parseInt(savedLastMaxCode.get()));
        } else {
            return generateComposedArticleCode(COMPOSED_PRODUCTION_CODE_INIT);
        }
    }

}
