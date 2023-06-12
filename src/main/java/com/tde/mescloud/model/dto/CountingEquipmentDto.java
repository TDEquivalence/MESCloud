package com.tde.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountingEquipmentDto {

    private long id;
    private String code;
    private String alias;
    //private Section section;
    private int equipmentStatus;
    private int pTimerCommunicationCycle;
    private boolean hasActiveProductionOrder;
    //TODO: Implement
    //List<EquipmentOutputDto> outputs;
}
