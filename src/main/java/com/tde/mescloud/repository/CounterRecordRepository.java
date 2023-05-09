package com.tde.mescloud.repository;

import com.tde.mescloud.model.entity.CounterRecordEntity;
import org.springframework.data.repository.CrudRepository;

public interface CounterRecordRepository extends CrudRepository<CounterRecordEntity, Long> {
}
