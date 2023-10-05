package com.alcegory.mescloud.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    private Integer theoreticalProduction;
    private Double qualityTarget;
    private Double availabilityTarget;
    private Double performanceTarget;
    private Double overallEquipmentEffectivenessTarget;
}