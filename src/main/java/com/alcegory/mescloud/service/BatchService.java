package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.BatchDto;
import com.alcegory.mescloud.model.dto.RejectRequestDto;
import com.alcegory.mescloud.model.dto.RequestBatchDto;
import com.alcegory.mescloud.model.dto.RequestComposedDto;
import com.alcegory.mescloud.model.entity.BatchEntity;

import java.util.List;
import java.util.Optional;

public interface BatchService {

    BatchEntity saveAndUpdate(BatchEntity batchEntity);

    void delete(BatchEntity batchEntity);

    Optional<BatchEntity> findById(Long id);

    BatchDto create(RequestBatchDto requestBatchDto);

    List<BatchDto> getAll();

    BatchDto rejectComposed(RejectRequestDto rejectRequestDto);
}
