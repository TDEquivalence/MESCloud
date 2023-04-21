package com.tde.mescloud.repository;

import com.tde.mescloud.entity.CounterRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CounterRecordRepository extends JpaRepository<CounterRecord, Long> {

    CounterRecord findByAlias(String alias);
}
