package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.HitDto;
import com.tde.mescloud.model.dto.RequestHitDto;
import com.tde.mescloud.model.entity.HitEntity;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Log
public class HitServiceImpl implements HitService {


    @Override
    public List<HitDto> create(RequestHitDto requestHitDto) {
        return null;
    }

    @Override
    public HitEntity saveAndUpdate(HitEntity hitEntity) {
        return null;
    }

    @Override
    public List<HitEntity> saveAndUpdateAll(List<HitEntity> hitEntity) {
        return null;
    }

    @Override
    public void delete(HitEntity hitEntity) {

    }

    @Override
    public Optional<HitEntity> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<HitDto> getAll() {
        return null;
    }

    @Override
    public List<HitDto> getAll() {
        return converter.convertToDto(repository.findAll());
    }
}
