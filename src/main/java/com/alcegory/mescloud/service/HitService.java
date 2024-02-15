package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.HitDto;
import com.alcegory.mescloud.model.request.RequestHitDto;
import com.alcegory.mescloud.model.entity.HitEntity;

import java.util.List;
import java.util.Optional;

public interface HitService {

    List<HitDto> create(RequestHitDto requestHitDto);

    HitEntity saveAndUpdate(HitEntity hitEntity);

    List<HitEntity> saveAndUpdateAll(List<HitEntity> hitEntity);

    void delete(HitEntity hitEntity);

    Optional<HitEntity> findById(Long id);

    List<HitDto> getAll();
}
