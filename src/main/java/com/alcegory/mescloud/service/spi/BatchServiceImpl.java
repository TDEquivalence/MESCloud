package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.model.dto.*;
import com.alcegory.mescloud.repository.BatchRepository;
import com.alcegory.mescloud.model.converter.GenericConverter;
import com.alcegory.mescloud.model.entity.BatchEntity;
import com.alcegory.mescloud.model.entity.ComposedProductionOrderEntity;
import com.alcegory.mescloud.service.BatchService;
import com.alcegory.mescloud.service.ComposedProductionOrderService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Log
public class BatchServiceImpl implements BatchService {

    private final BatchRepository repository;
    private final GenericConverter<BatchEntity, BatchDto> converter;

    private final ComposedProductionOrderService composedService;

    @Override
    public BatchDto create(RequestBatchDto requestBatch) {
        BatchEntity batch = createBatch(requestBatch);
        BatchEntity savedBatch = saveAndUpdate(batch);
        setProductionOrderApproval(savedBatch);
        return converter.toDto(savedBatch, BatchDto.class);
    }

    private BatchEntity createBatch(RequestBatchDto requestBatch) {
        BatchEntity batch = converter.toEntity(requestBatch.getBatch(), BatchEntity.class);
        batch.setComposedProductionOrder(getComposedById(requestBatch.getComposedId()));
        return batch;
    }

    public BatchDto rejectComposed(RejectRequestDto rejectRequestDto) {
        Optional<ComposedProductionOrderDto> composedProductionOrderDto = composedService.create(rejectRequestDto.getProductionOrderIds());

        if (composedProductionOrderDto.isEmpty()) {
            throw new IllegalArgumentException("Composed production order not created");
        }

        ComposedProductionOrderDto composed = composedProductionOrderDto.get();
        rejectRequestDto.getRequestBatchDto().setComposedId(composed.getId());
        return create(rejectRequestDto.getRequestBatchDto());
    }

    private ComposedProductionOrderEntity getComposedById(Long composedId) {
        Optional<ComposedProductionOrderEntity> composedOpt = composedService.findById(composedId);
        if (composedOpt.isEmpty()) {
            throw new IllegalStateException("Composed production order is null or empty");
        }
        return composedOpt.get();
    }

    private void setProductionOrderApproval(BatchEntity batch) {
        try {
            if (batch == null) {
                throw new IllegalArgumentException("Invalid batch or not approved");
            }

            if (batch.getComposedProductionOrder() == null) {
                throw new IllegalArgumentException("Composed batch is null");
            }

            composedService.setProductionOrderApproval(batch.getComposedProductionOrder(), batch.getIsApproved());
        } catch (IllegalArgumentException e) {
            log.warning("Production Order Approval failed: " + e.getMessage());
        }
    }

    @Override
    public BatchEntity saveAndUpdate(BatchEntity batch) {
        return repository.save(batch);
    }

    @Override
    public void delete(BatchEntity batch) {
        repository.delete(batch);
    }

    @Override
    public Optional<BatchEntity> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<BatchDto> getAll() {
        List<BatchEntity> batches = repository.findAll();
        return converter.toDto(batches, BatchDto.class);
    }
}
