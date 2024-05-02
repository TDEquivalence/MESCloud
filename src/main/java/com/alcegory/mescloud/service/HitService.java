package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.HitDto;
import com.alcegory.mescloud.model.dto.ProductionOrderDto;
import com.alcegory.mescloud.model.entity.HitEntity;
import com.alcegory.mescloud.model.request.RequestById;
import com.alcegory.mescloud.model.request.RequestHitDto;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface HitService {

    List<HitDto> create(RequestHitDto requestHitDto, Authentication authentication);

    HitEntity saveAndUpdate(HitEntity hitEntity);

    List<HitEntity> saveAndUpdateAll(List<HitEntity> hitEntity);

    void delete(HitEntity hitEntity);

    Optional<HitEntity> findById(Long id);

    List<HitDto> getAll();

    List<ProductionOrderDto> removeHits(RequestById request, Authentication authentication);
}
