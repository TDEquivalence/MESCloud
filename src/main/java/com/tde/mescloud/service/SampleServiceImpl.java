package com.tde.mescloud.service;

import com.tde.mescloud.model.converter.ComposedProductionOrderConverter;
import com.tde.mescloud.model.converter.SampleConverter;
import com.tde.mescloud.model.dto.ComposedProductionOrderDto;
import com.tde.mescloud.model.dto.RequestSampleDto;
import com.tde.mescloud.model.dto.filter.SampleDto;
import com.tde.mescloud.model.entity.ComposedProductionOrderEntity;
import com.tde.mescloud.model.entity.SampleEntity;
import com.tde.mescloud.repository.SampleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Log
public class SampleServiceImpl implements SampleService {

    private final SampleRepository repository;
    private final SampleConverter converter;

    private final ComposedProductionOrderService composedService;
    private final ComposedProductionOrderConverter composedConverter;

    @Override
    public Optional<SampleDto> create(RequestSampleDto requestSampleDto) {
        ComposedProductionOrderEntity composedEntity = createComposed(requestSampleDto);

        if(composedEntity == null) {
            return Optional.empty();
        }

        SampleDto sampleDto = new SampleDto();
        sampleDto.setAmount(requestSampleDto.getAmount());

        SampleEntity sampleEntity = converter.convertToEntity(sampleDto);
        sampleEntity.setComposedProductionOrder(composedEntity);
        repository.save(sampleEntity);

        return Optional.of(converter.convertToDto(sampleEntity));
    }

    private ComposedProductionOrderEntity createComposed(RequestSampleDto requestSampleDto) {
        Optional<ComposedProductionOrderDto> composedDto = composedService.create(requestSampleDto.getProductionOrders());
        if(composedDto.isEmpty()) {
            return null;
        }
        return composedConverter.convertToEntity(composedDto.get());
    }
}