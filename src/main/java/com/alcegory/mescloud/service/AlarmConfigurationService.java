package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.entity.AlarmConfigurationEntity;

import java.util.Optional;

public interface AlarmConfigurationService {

    Optional<AlarmConfigurationEntity> findByWordIndexAndBitIndex(int wordIndex, int bitIndex);
}
