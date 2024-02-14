package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.BatchDto;
import com.alcegory.mescloud.model.dto.RejectRequestDto;
import com.alcegory.mescloud.model.entity.BatchEntity;
import com.alcegory.mescloud.model.request.RequestBatchDto;

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
