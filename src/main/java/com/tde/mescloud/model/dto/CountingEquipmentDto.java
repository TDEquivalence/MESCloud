package com.tde.mescloud.model.dto;

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
    private int pTimerCommunicationCycle;
    private List<EquipmentOutputDto> outputs;
    private String imsCode;

    private Integer theoreticalProduction;
    private Double overallEquipmentEffectivenessTarget;
    private Double availabilityTarget;
    private Double performanceTarget;
    private Double qualityTarget;
}