package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.ImsDto;

import java.util.List;
import java.util.Optional;

public interface ImsService {

    Optional<ImsDto> create(ImsDto ims);

    List<ImsDto> getAll();

    Optional<ImsDto> findById(Long id);
}
