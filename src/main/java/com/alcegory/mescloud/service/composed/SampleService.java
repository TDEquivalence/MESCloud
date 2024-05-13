package com.alcegory.mescloud.service.composed;

import com.alcegory.mescloud.model.dto.ProductionOrderDto;
import com.alcegory.mescloud.model.dto.SampleDto;
import com.alcegory.mescloud.model.entity.SampleEntity;
import com.alcegory.mescloud.model.request.RequestById;
import com.alcegory.mescloud.model.request.RequestSampleDto;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface SampleService {

    SampleEntity saveAndUpdate(SampleEntity sampleEntity);

    void delete(SampleEntity sampleEntity);

    Optional<SampleEntity> findById(Long id);

    SampleDto create(RequestSampleDto requestSampleDto, Authentication authentication);

    List<SampleDto> getAll();

    SampleEntity findByComposedProductionOrderId(Long composedProductionOrderId);

    List<ProductionOrderDto> removeProductionOrderFromComposed(RequestById request, Authentication authentication);
}
