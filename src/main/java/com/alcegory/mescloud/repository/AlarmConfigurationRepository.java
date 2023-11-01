package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.entity.AlarmConfigurationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlarmConfigurationRepository extends JpaRepository<AlarmConfigurationEntity, Long> {

    Optional<AlarmConfigurationEntity> findByWordIndexAndBitIndex(int wordIndex, int bitIndex);
}
