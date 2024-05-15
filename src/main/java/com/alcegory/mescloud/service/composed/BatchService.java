package com.alcegory.mescloud.service.composed;

import com.alcegory.mescloud.model.dto.composed.BatchDto;
import com.alcegory.mescloud.model.dto.production.ProductionOrderDto;
import com.alcegory.mescloud.model.entity.composed.BatchEntity;
import com.alcegory.mescloud.model.request.RequestBatchDto;
import com.alcegory.mescloud.model.request.RequestById;
import com.alcegory.mescloud.model.request.RequestToRejectBatchDto;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface BatchService {

    BatchEntity saveAndUpdate(BatchEntity batchEntity);

    void delete(BatchEntity batchEntity);

    Optional<BatchEntity> findById(Long id);

    BatchDto create(RequestBatchDto requestBatchDto, Authentication authentication);

    List<BatchDto> getAll();

    BatchDto rejectComposed(RequestToRejectBatchDto requestToRejectBatchDto, Authentication authentication);

    List<ProductionOrderDto> removeBatch(RequestById request, Authentication authentication);
}
