package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.RequestSampleDto;
import com.tde.mescloud.model.dto.filter.SampleDto;

import java.util.Optional;

public interface SampleService {

    Optional<SampleDto> create(RequestSampleDto requestSampleDto);
}
