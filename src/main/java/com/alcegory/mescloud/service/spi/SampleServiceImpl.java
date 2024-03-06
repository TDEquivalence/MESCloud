package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.model.converter.GenericConverter;
import com.alcegory.mescloud.model.converter.ProductionOrderConverter;
import com.alcegory.mescloud.model.dto.ComposedProductionOrderDto;
import com.alcegory.mescloud.model.dto.ProductionOrderDto;
import com.alcegory.mescloud.model.dto.SampleDto;
import com.alcegory.mescloud.model.entity.ComposedProductionOrderEntity;
import com.alcegory.mescloud.model.entity.ProductionOrderEntity;
import com.alcegory.mescloud.model.entity.SampleEntity;
import com.alcegory.mescloud.model.request.RequestById;
import com.alcegory.mescloud.model.request.RequestSampleDto;
import com.alcegory.mescloud.repository.SampleRepository;
import com.alcegory.mescloud.service.ComposedProductionOrderService;
import com.alcegory.mescloud.service.ProductionOrderService;
import com.alcegory.mescloud.service.SampleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Log
public class SampleServiceImpl implements SampleService {

    private final SampleRepository repository;
    private final GenericConverter<SampleEntity, SampleDto> converter;
    private final GenericConverter<ComposedProductionOrderEntity, ComposedProductionOrderDto> composedConverter;
    private final ProductionOrderConverter productionOrderConverter;

    private final ComposedProductionOrderService composedService;
    private final ProductionOrderService productionOrderService;

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

    @Override
    public List<ProductionOrderDto> removeProductionOrderFromComposed(RequestById request) {

        Optional<ProductionOrderEntity> productionOrderOpt = productionOrderService.findById(request.getId());

        if (productionOrderOpt.isEmpty()) {
            throw new EntityNotFoundException("Production Order not found");
        }

        ProductionOrderEntity productionOrder = productionOrderOpt.get();
        ComposedProductionOrderEntity composedProductionOrder = productionOrder.getComposedProductionOrder();

        productionOrder.setComposedProductionOrder(null);
        productionOrderService.saveAndUpdate(productionOrder);

        List<ProductionOrderEntity> productionOrders = productionOrderService.findByComposedProductionOrderId(composedProductionOrder.getId());
        if (productionOrders.isEmpty()) {
            SampleEntity sampleEntity = repository.findByComposedProductionOrderId(composedProductionOrder.getId());
            repository.delete(sampleEntity);
            composedService.delete(composedProductionOrder);
            return Collections.emptyList();
        }

        return productionOrderConverter.toDto(productionOrders);
    }
}
