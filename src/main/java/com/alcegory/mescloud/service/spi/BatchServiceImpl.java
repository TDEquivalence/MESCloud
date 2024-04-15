package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.model.converter.GenericConverter;
import com.alcegory.mescloud.model.converter.ProductionOrderConverter;
import com.alcegory.mescloud.model.dto.BatchDto;
import com.alcegory.mescloud.model.dto.ComposedProductionOrderDto;
import com.alcegory.mescloud.model.dto.ProductionOrderDto;
import com.alcegory.mescloud.model.entity.BatchEntity;
import com.alcegory.mescloud.model.entity.ComposedProductionOrderEntity;
import com.alcegory.mescloud.model.entity.ProductionOrderEntity;
import com.alcegory.mescloud.model.request.RequestBatchDto;
import com.alcegory.mescloud.model.request.RequestById;
import com.alcegory.mescloud.model.request.RequestToRejectBatchDto;
import com.alcegory.mescloud.repository.BatchRepository;
import com.alcegory.mescloud.security.service.UserRoleService;
import com.alcegory.mescloud.service.BatchService;
import com.alcegory.mescloud.service.ComposedProductionOrderService;
import com.alcegory.mescloud.service.ProductionOrderService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.alcegory.mescloud.security.model.SectionAuthority.ADMIN_DELETE;

@Service
@AllArgsConstructor
@Log
public class BatchServiceImpl implements BatchService {

    private final BatchRepository repository;
    private final ComposedProductionOrderService composedService;
    private final ProductionOrderService productionOrderService;
    private final UserRoleService userRoleService;

    private final GenericConverter<BatchEntity, BatchDto> converter;
    private final ProductionOrderConverter productionOrderConverter;

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

    public BatchDto rejectComposed(RequestToRejectBatchDto requestToRejectBatchDto) {
        Optional<ComposedProductionOrderDto> composedProductionOrderDto = composedService.create(requestToRejectBatchDto.getProductionOrderIds());

        if (composedProductionOrderDto.isEmpty()) {
            throw new IllegalArgumentException("Composed production order not created");
        }

        ComposedProductionOrderDto composed = composedProductionOrderDto.get();
        requestToRejectBatchDto.getRequestBatchDto().setComposedId(composed.getId());
        return create(requestToRejectBatchDto.getRequestBatchDto());
    }

    @Override
    public List<ProductionOrderDto> removeBatch(RequestById request, Authentication authentication) {
        //TODO: sectionID
        userRoleService.checkAuthority(authentication, 1L, ADMIN_DELETE);
        Optional<ComposedProductionOrderEntity> composedOpt = composedService.findById(request.getId());

        if (composedOpt.isEmpty()) {
            return Collections.emptyList();
        }

        BatchEntity batch = repository.findByComposedProductionOrderId(composedOpt.get().getId());

        if (batch == null) {
            throw new IllegalArgumentException("Batch not found");
        }

        List<ProductionOrderEntity> productionOrders =
                productionOrderService.findByComposedProductionOrderId(composedOpt.get().getId());
        if (Boolean.FALSE.equals(batch.getIsApproved())) {
            for (ProductionOrderEntity productionOrder : productionOrders) {
                productionOrder.setComposedProductionOrder(null);
                productionOrderService.saveAndUpdate(productionOrder);
            }
        }

        repository.delete(batch);
        composedOpt.get().setApprovedAt(null);
        composedService.saveAndUpdate(composedOpt.get());

        return productionOrderConverter.toDto(productionOrders);
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
