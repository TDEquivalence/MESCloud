package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.HitDto;
import com.tde.mescloud.model.dto.RequestHitDto;
import com.tde.mescloud.model.entity.HitEntity;

import java.util.List;
import java.util.Optional;

public interface HitService {

    List<HitDto> create(RequestHitDto requestHitDto);

    HitEntity saveAndUpdate(HitEntity hitEntity);

    List<HitEntity> saveAndUpdateAll(List<HitEntity> hitEntity);

    void delete(HitEntity hitEntity);

    Optional<HitEntity> findById(Long id);
}
