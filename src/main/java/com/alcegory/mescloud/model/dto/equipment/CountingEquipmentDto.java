package com.alcegory.mescloud.model.dto.equipment;

import com.alcegory.mescloud.model.dto.company.FeatureDto;
import com.alcegory.mescloud.model.dto.ImsDto;
import com.alcegory.mescloud.model.entity.equipment.CountingEquipmentEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CountingEquipmentDto {

    private long id;
    private long sectionId;
    private String code;
    private String alias;
    private int equipmentStatus;
    private String productionOrderCode;
    @JsonProperty("pTimerCommunicationCycle")
    private int pTimerCommunicationCycle;
    private List<EquipmentOutputDto> outputs;
    private ImsDto ims;

    private Double theoreticalProduction;
    private Double qualityTarget;
    private Double availabilityTarget;
    private Double performanceTarget;
    private Double overallEquipmentEffectivenessTarget;
    private int unrecognizedAlarmDuration;
    @Enumerated(EnumType.STRING)
    private CountingEquipmentEntity.OperationStatus operationStatus;
    private List<FeatureDto> features;
}