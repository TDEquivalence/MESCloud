package com.alcegory.mescloud.service.alarm;

import com.alcegory.mescloud.model.entity.AlarmConfigurationEntity;

import java.util.Optional;

public interface AlarmConfigurationService {

    Optional<AlarmConfigurationEntity> findByEquipmentAndWordAndBitIndexes(Long equipmentId, int wordIndex, int bitIndex);
}
