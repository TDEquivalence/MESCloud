package com.alcegory.mescloud.service.composed;

import com.alcegory.mescloud.model.converter.GenericConverter;
import com.alcegory.mescloud.model.converter.ProductionOrderConverter;
import com.alcegory.mescloud.model.dto.composed.HitDto;
import com.alcegory.mescloud.model.dto.production.ProductionOrderDto;
import com.alcegory.mescloud.model.entity.composed.ComposedProductionOrderEntity;
import com.alcegory.mescloud.model.entity.composed.HitEntity;
<<<<<<< HEAD
import com.alcegory.mescloud.model.entity.production.ProductionOrderEntity;
import com.alcegory.mescloud.model.entity.composed.SampleEntity;
=======
import com.alcegory.mescloud.model.entity.composed.SampleEntity;
import com.alcegory.mescloud.model.entity.production.ProductionOrderEntity;
>>>>>>> test_environment
import com.alcegory.mescloud.model.request.RequestById;
import com.alcegory.mescloud.model.request.RequestHitDto;
import com.alcegory.mescloud.repository.composed.HitRepository;
import com.alcegory.mescloud.security.service.UserRoleService;
import com.alcegory.mescloud.service.production.ProductionOrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.alcegory.mescloud.security.model.SectionAuthority.ADMIN_DELETE;
import static com.alcegory.mescloud.security.model.SectionAuthority.OPERATOR_CREATE;

@Service
@AllArgsConstructor
@Log
public class HitServiceImpl implements HitService {

    public static final float TCA_LIMIT = 0.695f;
    private static final double INITIAL_VALUE = 0.0;

    private final SampleService sampleService;
    private final ComposedProductionOrderService composedService;
    private final ProductionOrderService productionOrderService;
    private final UserRoleService userRoleService;

    private final HitRepository repository;

    private final GenericConverter<HitEntity, HitDto> converter;
    private final ProductionOrderConverter productionOrderConverter;

    @Override
    public List<HitDto> create(long sectionId, RequestHitDto requestHitDto, Authentication authentication) {
        userRoleService.checkSectionAuthority(authentication, sectionId, OPERATOR_CREATE);

        if (requestHitDto.getHits() == null || requestHitDto.getHits().isEmpty()) {
            throw new IllegalArgumentException("Hits list are not valid");
        }

        if (!areAllSampleIdsEqual(requestHitDto.getHits())) {
            throw new IllegalArgumentException("Not all Sample Ids are equal");
        }

        return saveHitsAndUpdateSample(requestHitDto.getHits());
    }

    private boolean areAllSampleIdsEqual(List<HitDto> hitDtoList) {
        if (hitDtoList == null || hitDtoList.isEmpty()) {
            return false;
        }

        Long firstSampleId = hitDtoList.get(0).getComposedId();
        for (HitDto hitDto : hitDtoList) {
            if (!firstSampleId.equals(hitDto.getComposedId())) {
                return false;
            }
        }

        return true;
    }

    private List<HitDto> saveHitsAndUpdateSample(List<HitDto> hitList) {
        SampleEntity sample = getSample(hitList);
        List<HitEntity> hits = converter.toEntity(hitList, HitEntity.class);

        saveHitAndSetSample(sample, hits);
        setTcaAverageInSample(sample, hits);
        setReliability(sample);

        sampleService.saveAndUpdate(sample);
        setHitInsertAtInComposed(sample);
        return converter.toDto(hits, HitDto.class);
    }

    private void setReliability(SampleEntity sample) {
        List<HitEntity> validHitsAboveTcaLimit = repository.findValidHitsAboveTcaLimit(sample.getId(), TCA_LIMIT);
        double reliability = calculateSampleReliability(validHitsAboveTcaLimit.size(), sample.getAmount());
        sample.setReliability(reliability);
    }

    private double calculateSampleReliability(int numOfHitsAboveTcaLimit, int sampleAmount) {
        return (1 - (double) numOfHitsAboveTcaLimit / sampleAmount) * 100;
    }

    private void saveHitAndSetSample(SampleEntity sample, List<HitEntity> hits) {
        for (HitEntity hitEntity : hits) {
            hitEntity.setSample(sample);
        }
        saveAndUpdateAll(hits);
    }

    private void setHitInsertAtInComposed(SampleEntity sample) {
        if (sample == null) {
            throw new EntityNotFoundException("SampleEntity not found");
        }
        composedService.setHitInsertAtInComposed(sample.getComposedProductionOrder());
    }

    private void setTcaAverageInSample(SampleEntity sample, List<HitEntity> hits) {
        double tcaAverage = getTcaAverage(hits);
        sample.setTcaAverage(tcaAverage);
    }

    private double getTcaAverage(List<HitEntity> hits) {
        if (hits == null || hits.isEmpty()) {
            return 0.0;
        }

        float tcaSum = 0;
        for (HitEntity hit : hits) {
            tcaSum += hit.getTca();
        }

        return tcaSum / hits.size();
    }

    private SampleEntity getSample(List<HitDto> hitList) {
        HitDto hitDto = hitList.get(0);
        Long composedId = hitDto.getComposedId();
        SampleEntity sample = sampleService.findByComposedProductionOrderId(composedId);

        if (sample == null) {
            throw new IllegalArgumentException("Sample not found");
        }

        return sample;
    }

    @Override
    public List<ProductionOrderDto> removeHits(long sectionId, RequestById request, Authentication authentication) {
        userRoleService.checkSectionAuthority(authentication, sectionId, ADMIN_DELETE);
        Optional<ComposedProductionOrderEntity> composedOpt = composedService.findById(request.getId());

        if (composedOpt.isEmpty()) {
            return Collections.emptyList();
        }

        SampleEntity sample = sampleService.findByComposedProductionOrderId(composedOpt.get().getId());

        if (sample == null) {
            throw new IllegalArgumentException("Sample not found in remove hits");
        }

        List<HitEntity> hits = repository.findBySampleId(sample.getId());
        deleteAll(hits);
        resetSample(sample);

        List<ProductionOrderEntity> productionOrders =
                productionOrderService.findByComposedProductionOrderId(composedOpt.get().getId());

        return productionOrderConverter.toDto(productionOrders);
    }

    private void resetSample(SampleEntity sample) {
        sample.setReliability(INITIAL_VALUE);
        sample.setTcaAverage(INITIAL_VALUE);
        sampleService.saveAndUpdate(sample);
    }

    public HitEntity saveAndUpdate(HitEntity hitEntity) {
        return repository.save(hitEntity);
    }

    @Override
    public List<HitEntity> saveAndUpdateAll(List<HitEntity> hitList) {
        return repository.saveAll(hitList);
    }

    @Override
    public void delete(HitEntity hitEntity) {
        repository.delete(hitEntity);
    }

    private void deleteAll(List<HitEntity> hits) {
        repository.deleteAll(hits);
    }

    @Override
    public Optional<HitEntity> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<HitDto> getAll() {
        return converter.toDto(repository.findAll(), HitDto.class);
    }

}
