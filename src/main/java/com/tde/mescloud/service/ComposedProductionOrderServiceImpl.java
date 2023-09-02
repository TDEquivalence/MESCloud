package com.tde.mescloud.service;

import com.tde.mescloud.model.converter.ComposedProductionOrderConverter;
import com.tde.mescloud.model.dto.ComposedProductionOrderDto;
import com.tde.mescloud.model.dto.ProductionOrderDto;
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

    private final ComposedProductionOrderRepository composedRepository;
    private final ComposedProductionOrderConverter converter;

    private final ProductionOrderRepository productionOrderRepository;

    private static final String CODE_PREFIX = "CP";
    private static final int CODE_INITIAL_VALUE = 0;

    @Override
    public Optional<ComposedProductionOrderDto> create(ProductionOrderDto[] requestComposedDto) {
        ComposedProductionOrderEntity composedEntity = createComposed();

        for(ProductionOrderDto po : requestComposedDto) {
            Optional<ProductionOrderEntity> productionOrderEntity = productionOrderRepository.findById(po.getId());
            if(productionOrderEntity.isEmpty()) {
                continue;
            }
            productionOrderEntity.get().setComposedProductionOrder(composedEntity);
            composedEntity.getProductionOrderEntity().add(productionOrderEntity.get());
            productionOrderRepository.save(productionOrderEntity.get());
        }

        composedRepository.save(composedEntity);
        if(composedEntity.getProductionOrderEntity() == null) {
            composedRepository.delete(composedEntity);
            return Optional.empty();
        }

        return Optional.of(converter.convertToDto(composedEntity));
    }

    private ComposedProductionOrderEntity createComposed() {
        ComposedProductionOrderDto composedDto = new ComposedProductionOrderDto();
        String composedProductionCode = generateCode();
        composedDto.setCode(composedProductionCode);

        ComposedProductionOrderEntity composedEntity = converter.convertToEntity(composedDto);
        return composedRepository.save(composedEntity);
    }

    private String incrementAndGenerateCode(int lastMaxCode) {
        int codeIncremented = lastMaxCode + 1;
        return CODE_PREFIX + String.format("%05d", codeIncremented);
    }

    private String generateCode() {
        Optional<String> savedLastMaxCode = composedRepository.findLastMaxCode();

        if (savedLastMaxCode.isPresent()) {
            return incrementAndGenerateCode(Integer.parseInt(savedLastMaxCode.get()));
        } else {
            return incrementAndGenerateCode(CODE_INITIAL_VALUE);
        }
    }

}
