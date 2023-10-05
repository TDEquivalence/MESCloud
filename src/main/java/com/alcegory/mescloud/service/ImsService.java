package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.ImsDto;
import com.alcegory.mescloud.model.entity.ImsEntity;

import java.util.List;
import java.util.Optional;

public interface ImsService {

    ImsDto create(ImsDto ims);

    List<ImsDto> getAll();

    Optional<ImsDto> findById(Long id);

    Optional<ImsEntity> findByCode(String code);

    boolean isValidAndFree(Long imsId);
}
