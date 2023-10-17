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

    AlarmConfigurationRepository repository;

    @Override
    public Optional<AlarmConfigurationEntity> findByWordIndexAndBitIndex(int wordIndex, int bitIndex) {
        return repository.findByWordIndexAndBitIndex(wordIndex, bitIndex);
    }
}
