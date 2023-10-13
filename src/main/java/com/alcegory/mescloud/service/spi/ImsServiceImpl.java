package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.repository.ImsRepository;
import com.alcegory.mescloud.model.converter.GenericConverter;
import com.alcegory.mescloud.model.dto.ImsDto;
import com.alcegory.mescloud.model.entity.ImsEntity;
import com.alcegory.mescloud.service.ImsService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log
@AllArgsConstructor
public class ImsServiceImpl implements ImsService {

    private static final int MIN_CODE_SIZE = 3;

    private GenericConverter<ImsEntity, ImsDto> converter;
    private ImsRepository repository;

    @Override
    public List<ImsDto> getAll() {
        List<ImsEntity> entities = repository.findAll();
        return converter.toDto(entities, ImsDto.class);
    }

    @Override
    public Optional<ImsDto> findById(Long id) {
        Optional<ImsEntity> persistedImsOpt = repository.findById(id);
        if (persistedImsOpt.isEmpty()) {
            log.warning(String.format("Unable to find IMS with id [%s]", id));
            return Optional.empty();
        }

        ImsDto ims = converter.toDto(persistedImsOpt.get(), ImsDto.class);
        return Optional.of(ims);
    }

    @Override
    public Optional<ImsEntity> findByCode(String code) {
        return repository.findByCode(code);
    }

    @Override
    public boolean isValidAndFree(Long imsId) {
        Optional<ImsEntity> imsEntityOpt = repository.findById(imsId);
        if (imsEntityOpt.isEmpty()) {
            log.warning(String.format("No IMS found with id [%s]", imsId));
            return false;
        }

        ImsEntity imsEntity = imsEntityOpt.get();
        if (imsEntity.isInUse()) {
            log.warning(String.format("IMS already in use at Counting Equipment with id [%s]", imsEntity.getCountingEquipment().getId()));
            return false;
        }

        return true;
    }

    public ImsDto create(ImsDto imsDto) {

        if (imsDto.getCode() == null || imsDto.getCode().length() < MIN_CODE_SIZE) {
            log.warning(String.format("Invalid IMS code: [%s]", imsDto.getCode()));
            return null;
        }

        ImsEntity imsEntity = converter.toEntity(imsDto, ImsEntity.class);
        ImsEntity persistedIms = repository.save(imsEntity);
        return converter.toDto(persistedIms, ImsDto.class);
    }
}