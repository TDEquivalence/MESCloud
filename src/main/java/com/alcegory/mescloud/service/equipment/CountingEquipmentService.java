package com.alcegory.mescloud.service.equipment;

import com.alcegory.mescloud.model.dto.equipment.CountingEquipmentDto;
import com.alcegory.mescloud.model.entity.equipment.CountingEquipmentEntity;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CountingEquipmentService {

<<<<<<< HEAD
    List<CountingEquipmentDto> findAllWithLastProductionOrder();
=======
    List<CountingEquipmentDto> findAllWithLastProductionOrder(long sectionId);
>>>>>>> test_environment

    Optional<CountingEquipmentDto> findById(long id);

    Optional<CountingEquipmentDto> findByCode(String code);

    CountingEquipmentEntity save(CountingEquipmentEntity countingEquipment);

    CountingEquipmentDto save(CountingEquipmentDto countingEquipment);

    CountingEquipmentDto setOperationStatus(CountingEquipmentEntity countingEquipment, CountingEquipmentEntity.OperationStatus status);

    void setOperationStatusByCode(String equipmentCode, CountingEquipmentEntity.OperationStatus idle);

    List<Long> findAllIds();

    Long findIdByAlias(String alias);

    Optional<CountingEquipmentEntity> findEntityById(Long equipmentId);

    Optional<CountingEquipmentEntity> findByCodeWithLastStatusRecord(@Param("code") String code);

    Optional<CountingEquipmentEntity> findEntityByCode(String code);

    Optional<CountingEquipmentEntity> findByIdWithLastProductionOrder(@Param("id") Long id);

    boolean hasActiveProductionOrder(CountingEquipmentEntity countingEquipment);

    CountingEquipmentDto saveAndGetDto(CountingEquipmentEntity countingEquipment);

    Optional<CountingEquipmentEntity> findEquipmentTemplate(long id);

    Double getAverageQualityTargetDividedByTotalCount();

    Double getAverageAvailabilityTargetDividedByTotalCount();

    Double getAveragePerformanceTargetDividedByTotalCount();

    Double getAverageOverallEquipmentEffectivenessTargetDividedByTotalCount();

    Double getAverageTheoreticalProduction();
}
