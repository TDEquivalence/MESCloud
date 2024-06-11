package com.alcegory.mescloud.service.composed;

import com.alcegory.mescloud.model.dto.production.ProductionOrderDto;
import com.alcegory.mescloud.model.dto.composed.SampleDto;
import com.alcegory.mescloud.model.entity.composed.SampleEntity;
import com.alcegory.mescloud.model.request.RequestById;
import com.alcegory.mescloud.model.request.RequestSampleDto;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface SampleService {

    SampleEntity saveAndUpdate(SampleEntity sampleEntity);

    void delete(SampleEntity sampleEntity);

    Optional<SampleEntity> findById(Long id);

    SampleDto create(long sectionId, RequestSampleDto requestSampleDto, Authentication authentication);

    List<SampleDto> getAll();

    SampleEntity findByComposedProductionOrderId(Long composedProductionOrderId);

    List<ProductionOrderDto> removeProductionOrderFromComposed(long sectionId, RequestById request, Authentication authentication);
}