package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.repository.BatchRepository;
import com.alcegory.mescloud.model.converter.GenericConverter;
import com.alcegory.mescloud.model.dto.BatchDto;
import com.alcegory.mescloud.model.dto.RequestBatchDto;
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
        batch.setComposed(getComposedById(requestBatch.getComposedId()));
        return batch;
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

            if (batch.getComposed() == null) {
                throw new IllegalArgumentException("Composed batch is null");
            }

            composedService.setProductionOrderApproval(batch.getComposed(), batch.getIsApproved());
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
