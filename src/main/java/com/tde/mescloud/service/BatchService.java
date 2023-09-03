package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.BatchDto;
import com.tde.mescloud.model.dto.RequestBatchDto;
import com.tde.mescloud.model.entity.BatchEntity;

import java.util.List;
import java.util.Optional;

public interface BatchService {

    BatchEntity saveAndUpdate(BatchEntity batchEntity);

    void delete(BatchEntity batchEntity);

    Optional<BatchEntity> findById(Long id);

    BatchDto create(RequestBatchDto requestBatchDto);

    List<BatchDto> getAll();
}
