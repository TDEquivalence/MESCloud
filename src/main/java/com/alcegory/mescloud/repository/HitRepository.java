package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.entity.HitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<HitEntity, Long> {

    @Query(value = "SELECT * FROM hit h WHERE (h.sample_id = :sampleId AND h.is_valid_for_reliability = true AND h.tca > :tcaLimit)",
            nativeQuery = true)
    List<HitEntity> findValidHitsAboveTcaLimit(@Param("sampleId") Long sampleId, @Param("tcaLimit") Float tcaLimit);

    List<HitEntity> findBySampleId(Long sampleId);
}
