package com.alcegory.mescloud.service.composed;

import com.alcegory.mescloud.model.converter.GenericConverter;
import com.alcegory.mescloud.model.converter.ProductionOrderConverter;
import com.alcegory.mescloud.model.dto.composed.BatchDto;
import com.alcegory.mescloud.model.dto.composed.ComposedProductionOrderDto;
import com.alcegory.mescloud.model.dto.production.ProductionOrderDto;
import com.alcegory.mescloud.model.entity.composed.BatchEntity;
import com.alcegory.mescloud.model.entity.composed.ComposedProductionOrderEntity;
import com.alcegory.mescloud.model.entity.production.ProductionOrderEntity;
import com.alcegory.mescloud.model.request.RequestBatchDto;
import com.alcegory.mescloud.model.request.RequestById;
import com.alcegory.mescloud.model.request.RequestToRejectBatchDto;
import com.alcegory.mescloud.repository.composed.BatchRepository;
import com.alcegory.mescloud.security.service.UserRoleService;
import com.alcegory.mescloud.service.production.ProductionOrderService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.alcegory.mescloud.security.model.SectionAuthority.ADMIN_DELETE;
import static com.alcegory.mescloud.security.model.SectionAuthority.OPERATOR_CREATE;

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
<<<<<<< HEAD
    public BatchDto create(RequestBatchDto requestBatch, Authentication authentication) {
        //TODO: sectionID
        userRoleService.checkSectionAuthority(authentication, 1L, OPERATOR_CREATE);
=======
    public BatchDto create(long sectionId, RequestBatchDto requestBatch, Authentication authentication) {
        userRoleService.checkSectionAuthority(authentication, sectionId, OPERATOR_CREATE);
>>>>>>> test_environment

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

    public BatchDto rejectComposed(long sectionId, RequestToRejectBatchDto requestToRejectBatchDto, Authentication authentication) {
        Optional<ComposedProductionOrderDto> composedProductionOrderDto = composedService.create(requestToRejectBatchDto.getProductionOrderIds());

        if (composedProductionOrderDto.isEmpty()) {
            throw new IllegalArgumentException("Composed production order not created");
        }

        ComposedProductionOrderDto composed = composedProductionOrderDto.get();
        requestToRejectBatchDto.getRequestBatchDto().setComposedId(composed.getId());
        return create(sectionId, requestToRejectBatchDto.getRequestBatchDto(), authentication);
    }

    @Override
    public List<ProductionOrderDto> removeBatch(long sectionId, RequestById request, Authentication authentication) {
        userRoleService.checkSectionAuthority(authentication, sectionId, ADMIN_DELETE);
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
