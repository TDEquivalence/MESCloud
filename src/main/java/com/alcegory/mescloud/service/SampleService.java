package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.RequestSampleDto;
import com.alcegory.mescloud.model.dto.SampleDto;
import com.alcegory.mescloud.model.entity.SampleEntity;

import java.util.List;
import java.util.Optional;

public interface SampleService {

    SampleEntity saveAndUpdate(SampleEntity sampleEntity);

    void delete(SampleEntity sampleEntity);

    Optional<SampleEntity> findById(Long id);

    SampleDto create(RequestSampleDto requestSampleDto);

    List<SampleDto> getAll();
}
