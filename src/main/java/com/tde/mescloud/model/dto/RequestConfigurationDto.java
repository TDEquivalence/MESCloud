package com.tde.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestConfigurationDto {

    private long id;
    private String alias;
    private int pTimerCommunicationCycle;

    private List<EquipmentOutputDto> outputs;
    private ImsDto imsDto;

    private Double equipmentEffectiveness;
    private Integer theoreticalProduction;
    private Double availability;
    private Double performance;
    private Double quality;
}
