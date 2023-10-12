package com.alcegory.mescloud.service;

import com.alcegory.mescloud.repository.SampleRepository;
import com.alcegory.mescloud.model.converter.GenericConverter;
import com.alcegory.mescloud.model.dto.ComposedProductionOrderDto;
import com.alcegory.mescloud.model.dto.RequestSampleDto;
import com.alcegory.mescloud.model.dto.SampleDto;
import com.alcegory.mescloud.model.entity.ComposedProductionOrderEntity;
import com.alcegory.mescloud.model.entity.SampleEntity;
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
        SampleDto sampleDto = converter.toDto(sampleEntity, SampleDto.class);
        sampleDto.setComposedCode(composedEntity.getCode());
        return sampleDto;
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
