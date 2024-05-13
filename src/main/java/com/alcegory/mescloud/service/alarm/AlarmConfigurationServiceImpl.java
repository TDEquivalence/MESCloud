package com.alcegory.mescloud.service.alarm;

import com.alcegory.mescloud.model.entity.AlarmConfigurationEntity;
import com.alcegory.mescloud.repository.alarm.AlarmConfigurationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AlarmConfigurationServiceImpl implements AlarmConfigurationService {

    private final AlarmConfigurationRepository repository;

    @Override
    public Optional<AlarmConfigurationEntity> findByEquipmentAndWordAndBitIndexes(Long equipmentId, int wordIndex, int bitIndex) {
        return repository.findByEquipmentIdAndWordIndexAndBitIndex(equipmentId, wordIndex, bitIndex);
    }
}
