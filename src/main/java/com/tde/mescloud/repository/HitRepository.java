package com.tde.mescloud.repository;

import com.tde.mescloud.model.entity.HitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< HEAD
import org.springframework.stereotype.Repository;

@Repository
public interface HitRepository extends JpaRepository<HitEntity, Long> {
=======
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<HitEntity, Long> {

    @Query(value = "SELECT * FROM hit h WHERE (h.sample_id = :sampleId AND h.is_valid_for_reliability = true AND h.tca > :tcaLimit)", nativeQuery = true)
    List<HitEntity> findValidHitsAboveTcaLimit(@Param("sampleId") Long sampleId, @Param("tcaLimit") Float tcaLimit);
>>>>>>> development
}
