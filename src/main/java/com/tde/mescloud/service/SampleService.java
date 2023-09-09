package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.RequestSampleDto;
import com.tde.mescloud.model.dto.SampleDto;
import com.tde.mescloud.model.entity.SampleEntity;

import java.util.List;
import java.util.Optional;

public interface SampleService {

    SampleEntity saveAndUpdate(SampleEntity sampleEntity);

    void delete(SampleEntity sampleEntity);

    Optional<SampleEntity> findById(Long id);

    Optional<SampleDto> create(RequestSampleDto requestSampleDto);
    List<SampleDto> getAll();
}
