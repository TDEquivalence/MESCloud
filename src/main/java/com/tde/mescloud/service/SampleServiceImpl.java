package com.tde.mescloud.service;

import com.tde.mescloud.model.converter.GenericConverter;
import com.tde.mescloud.model.dto.ComposedProductionOrderDto;
import com.tde.mescloud.model.dto.RequestSampleDto;
import com.tde.mescloud.model.dto.SampleDto;
import com.tde.mescloud.model.entity.ComposedProductionOrderEntity;
import com.tde.mescloud.model.entity.SampleEntity;
import com.tde.mescloud.repository.SampleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Log
public class SampleServiceImpl implements SampleService {

    private final SampleRepository repository;
    private final GenericConverter<SampleEntity, SampleDto> converter;

    private final ComposedProductionOrderService composedService;
    private final GenericConverter<ComposedProductionOrderEntity, ComposedProductionOrderDto> composedConverter;

    @Override
    public SampleDto create(RequestSampleDto requestSampleDto) {
        ComposedProductionOrderEntity composedEntity = createComposed(requestSampleDto);
        return createSample(requestSampleDto, composedEntity);
    }

    private SampleDto createSample(RequestSampleDto requestSampleDto, ComposedProductionOrderEntity composedEntity) {
        SampleEntity sampleEntity = new SampleEntity();
        sampleEntity.setAmount(requestSampleDto.getAmount());
        sampleEntity.setComposedProductionOrder(composedEntity);
        sampleEntity.setCreatedAt(new Date());

        saveAndUpdate(sampleEntity);
        return converter.toDto(sampleEntity, SampleDto.class);
    }

    private ComposedProductionOrderEntity createComposed(RequestSampleDto requestSampleDto) {
        Optional<ComposedProductionOrderDto> composedDto = composedService.create(requestSampleDto.getProductionOrderIds());
        if (composedDto.isEmpty()) {
            throw new IllegalStateException("Composed Production Order creation error");
        }
        return composedConverter.toEntity(composedDto.get(), ComposedProductionOrderEntity.class);
    }

    public SampleEntity saveAndUpdate(SampleEntity sample) {
        return repository.save(sample);
    }

    @Override
    public void delete(SampleEntity sampleEntity) {
        repository.delete(sampleEntity);
    }

    @Override
    public Optional<SampleEntity> findById(Long id) {
        return repository.findById(id);
    }


    @Override
    public List<SampleDto> getAll() {
        return converter.toDto(repository.findAll(), SampleDto.class);
    }
}
