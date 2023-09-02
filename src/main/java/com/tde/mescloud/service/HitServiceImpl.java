package com.tde.mescloud.service;

import com.tde.mescloud.model.converter.HitConverter;
import com.tde.mescloud.model.dto.HitDto;
import com.tde.mescloud.model.dto.RequestHitDto;
import com.tde.mescloud.model.entity.HitEntity;
import com.tde.mescloud.model.entity.SampleEntity;
import com.tde.mescloud.repository.HitRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Log
public class HitServiceImpl implements HitService {

    private final SampleService sampleService;
    private final HitRepository repository;
    private final HitConverter converter;

    @Override
    public List<HitDto> create(RequestHitDto requestHitDto) {
        if (!areAllSampleIdsEqual(requestHitDto.getHitDtoList())) {
            throw new IllegalArgumentException("Not all sampleIds are equal");
        }

        return setSampleForHitEntities(requestHitDto.getHitDtoList());
    }

    private boolean areAllSampleIdsEqual(List<HitDto> hitDtoList) {
        if (hitDtoList == null || hitDtoList.isEmpty()) {
            return false;
        }

        Long firstSampleId = hitDtoList.get(0).getSampleId();
        for (HitDto hitDto : hitDtoList) {
            if (!firstSampleId.equals(hitDto.getSampleId())) {
                return false;
            }
        }

        return true;
    }

    private List<HitDto> setSampleForHitEntities(List<HitDto> hitList) {
        SampleEntity sample = getSample(hitList);
        List<HitEntity> hits = converter.convertToEntity(hitList);
        int tcaAverage = (int) getTcaAverage(hits);

        for (HitEntity hitEntity : hits) {
            hitEntity.setSample(sample);
        }

        sample.setTcaAverage(tcaAverage);
        sampleService.saveAndUpdate(sample);

        saveAndUpdateAll(hits);
        return converter.convertToDto(hits);
    }

    private SampleEntity getSample(List<HitDto> hitList) {
        HitDto hitDto = hitList.get(0);
        Optional<SampleEntity> sample = sampleService.findById(hitDto.getSampleId());
        if(sample.isEmpty()) {
            throw new IllegalArgumentException("Sample not found");
        }

        return sample.get();
    }

    private float getTcaAverage(List<HitEntity> hits) {
        if (hits == null || hits.isEmpty()) {
            return 0.0f;
        }

        float tcaSum = 0;
        for (HitEntity hit : hits) {
            tcaSum += hit.getTca();
        }

        return tcaSum / hits.size();
    }

    public HitEntity saveAndUpdate(HitEntity hitEntity) {
        return repository.save(hitEntity);
    }

    @Override
    public List<HitEntity> saveAndUpdateAll(List<HitEntity> hitList) {
        return repository.saveAll(hitList);
    }

    public void delete(HitEntity hitEntity) {
        repository.delete(hitEntity);
    }

    public Optional<HitEntity> findById(Long id) {
        return repository.findById(id);
    }
}