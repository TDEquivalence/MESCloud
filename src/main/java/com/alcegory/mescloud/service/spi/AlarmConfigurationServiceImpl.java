package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.model.entity.AlarmConfigurationEntity;
import com.alcegory.mescloud.repository.AlarmConfigurationRepository;
import com.alcegory.mescloud.service.AlarmConfigurationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AlarmConfigurationServiceImpl implements AlarmConfigurationService {

    private final AlarmConfigurationRepository repository;

    @Override
    public Optional<AlarmConfigurationEntity> findByWordAndBitIndexes(int wordIndex, int bitIndex) {
        return repository.findByWordIndexAndBitIndex(wordIndex, bitIndex);
    }
}
